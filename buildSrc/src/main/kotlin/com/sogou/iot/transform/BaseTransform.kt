package com.sogou.iot.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.sogou.iot.TrLogger
import com.sogou.iot.Utils
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * 文件名:BaseTransform
 * 创建者:baixuefei
 * 创建日期:2021/1/21 9:54 PM
 * 职责描述:
 */


abstract class BaseTransform : Transform() {


    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    //对外声明,支持增量编译
    override fun isIncremental(): Boolean {
        return true
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {

        //是否是增量编译
        val isCurrentIncremental = transformInvocation?.isIncremental

        if(isCurrentIncremental == false){ //如果是非增量编译,则清空就的输出内容
            transformInvocation?.outputProvider?.deleteAll()
        }

        //TransformInput 包含两个类型的输入:jar文件和文件夹
        transformInvocation?.inputs?.forEach { input ->

            //jar输入,它代表着以jar包方式参与项目编译的所有本地jar包或远程jar包，
            input.jarInputs.forEach { jarInput ->
                //确定输出文件
                val destFile = transformInvocation.outputProvider.getContentLocation(
                    calculateDestName(jarInput),//计算输出jar的名称
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )

                //文件变化状态
                var status = jarInput.status

                TrLogger.e("jarInput isCurrentIncremental:${isCurrentIncremental},status:${status},input:${jarInput.file.absolutePath},dest:${destFile.absolutePath}")

                if(isCurrentIncremental == true){ //增量编译
                    if(status == Status.NOTCHANGED){ //NOTCHANGED 不做任何处理
                        //do nothing
                    }else if(status == Status.ADDED || status == Status.CHANGED){ //ADDED或CHANGED做常规的处理
                        transformJar(jarInput.file,destFile)
                    }else if(status == Status.REMOVED){ //REMOVED 被移除,确保destFile被删除
                        if(destFile.exists()){
                            FileUtils.deleteRecursivelyIfExists(destFile)
                        }
                    }
                }else{ //非编译则执行,常规的全量处理
                    transformJar(jarInput.file,destFile)
                }

            }

            //目录输入,它代表着以源码方式参与项目编译的所有目录结构及其目录下的源码文件
            input.directoryInputs.forEach { directoryInput ->

                //确定输出文件des
                val dest: File = transformInvocation.getOutputProvider().getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )

                //完成从source到dest的拷贝操作
                TrLogger.e("DirectoryInput isCurrentIncremental:${isCurrentIncremental} inputDirectory:${directoryInput.file.absolutePath},dest:${dest.absolutePath}")

                if(isCurrentIncremental == true){ //增量编译,仅遍历发生变化的文件列表
                    var srcDirPath = directoryInput.file.absolutePath
                    var desDirPath = dest.absolutePath
                    directoryInput.changedFiles.forEach { inputFile, status ->
                        TrLogger.d("DirectoryInput isCurrentIncremental:${isCurrentIncremental},changedFiles,status:${status},${inputFile.absolutePath}")
                        val destFilePath = inputFile.absolutePath.replace(srcDirPath, desDirPath)
                        val destFile = File(destFilePath)

                        if(status == Status.NOTCHANGED){ //NOTCHANGED do nothing
                            //do nothing
                        }else if(status == Status.REMOVED){ //REMOVED 确保输出文件 被删除
                            if(destFile.exists()){
                                FileUtils.deleteRecursivelyIfExists(destFile)
                            }
                        }else if(status == Status.ADDED || status == Status.CHANGED){ //针对单个文件进行处理处理
                            transformSingleFile(inputFile,destFile,srcDirPath)
                        }
                    }
                }else{ //非增量编译,进行全量的Dir扫描
                    transfromDir(directoryInput.file,dest)
                }
            }

        }
    }


    fun calculateDestName(jarInput:JarInput):String{
        val destName = jarInput.name.let {
            //jar文件去掉.jar后缀
            if (it.endsWith(".jar")) it.substring(0, it.length - 4) else it

        }
        //确定输出文件名
        val finalDestName = "${destName}_${DigestUtils.md5Hex(jarInput.file.absolutePath)}"
        return finalDestName
    }

    fun transformJar(jarInputFile: File,destFile:File){
        var dealedJar = walkJar(jarInputFile) {
            doScanClass(it)
        }
        //通用操作,将jar文件 从输入copy到输出目的地
        FileUtils.copyFile(dealedJar, destFile)
    }

    fun transfromDir(dirInputFile:File,dirDestFile:File){
        walkDiretory(dirInputFile) {
            doScanClass(it)
        }
        FileUtils.copyDirectory(dirInputFile, dirDestFile)
    }

    fun transformSingleFile(dirInputFile:File,dirDestFile:File,srcDirectorPath:String){
        dealWithSingleFile(dirInputFile,dirDestFile,srcDirectorPath){
            doScanClass(it)
        }
        FileUtils.copyFile(dirInputFile, dirDestFile)
    }


    fun walkJar(inputJar: File, scanClass: ScanClassAction): File {
        var srcJar = JarFile(inputJar)

        //临时输出文件
        var outputJar = File(inputJar.parentFile.absolutePath + File.separator + "classes_tmp.jar")
        if (outputJar.exists()) outputJar.delete()
        //输出流
        var outputJarStream = JarOutputStream(FileOutputStream(outputJar))

        srcJar.entries()?.apply {
            while (hasMoreElements()) {

                //输入文件
                var inputJarEntry = nextElement()

                var inputJarStream = srcJar.getInputStream(inputJarEntry)

                //取出每一个class类，注意这里的包名是"/"分割 ，不是"."
                var entryName = inputJarEntry.name
                TrLogger.d("checkJar entryName ===>:${entryName}")
                //输出文件
                var outputZipEntry = ZipEntry(entryName)
                outputJarStream.putNextEntry(outputZipEntry)

                //目录不需要处理
                if (shouldScanJar(inputJarEntry)) { //需要扫描
                    val newClassBytes = scanClass(inputJarStream)
                    outputJarStream.write(newClassBytes)
                } else {
                    outputJarStream.write(IOUtils.toByteArray(inputJarStream))
                }

                outputJarStream.closeEntry()

            }
        }

        outputJarStream.close()
        srcJar.close()
        return outputJar
    }


    fun dealWithSingleFile(inputFile:File,destFile: File,srcDirectorPath:String, scanClass: ScanClassAction){
        val rootPath = srcDirectorPath.let {
            if (!it.endsWith(File.separator)) it + File.separator else it
        }
        if( inputFile.isFile && inputFile.name.endsWith(".class")){
            var className = Utils.getClassNameForFile(rootPath, inputFile)
            if (shouldScanDirectoryFile(className)) {
                var fis = FileInputStream(inputFile)
                var newClassBytes = scanClass(fis)
                var fos =
                    FileOutputStream(inputFile.parentFile.absolutePath + File.separator + inputFile.name)
                fos.write(newClassBytes)
                fos.close()
                fis.close()
            }
        }
    }


    fun walkDiretory(directory: File, scanClass: ScanClassAction) {
        val rootPath = directory.absolutePath.let {
            if (!it.endsWith(File.separator)) it + File.separator else it
        }
        TrLogger.d("checkDirecory rootPath:${rootPath}")

        //遍历文件目录
        directory.walk().filter {
            it.isFile && it.name.endsWith(".class")
        }.forEach { file ->
            var className = Utils.getClassNameForFile(rootPath, file)
            if (shouldScanDirectoryFile(className)) {
                var fis = FileInputStream(file)
                var newClassBytes = scanClass(fis)
                var fos =
                    FileOutputStream(file.parentFile.absolutePath + File.separator + file.name)
                fos.write(newClassBytes)
                fos.close()
                fis.close()
            }

            TrLogger.d("checkDirecory className-->:${className}")
        }
    }


    fun shouldScanJar(inputjar: JarEntry): Boolean {
        val should =
            !inputjar.isDirectory && inputjar.name.endsWith(".class") && !isSystemClass(inputjar.name)
        if (should) {
            TrLogger.d("shouldScan ${should} ---  Jar,name:${inputjar.name}")
        }
        return should
    }

    fun shouldScanDirectoryFile(fileName: String): Boolean {
        var should= !isSystemClass(fileName)
        if (should) {
            TrLogger.d("shouldScan  true ---  DirectoryFile:${fileName}")
        }
        return should
    }

    fun isSystemClass(name: String): Boolean {
        return name.startsWith("androidx") || name.startsWith("kotlin")
                || name.startsWith("org") || name.startsWith("android")
                || name.contains("R.class") || name.contains("R$")
    }


    abstract fun doScanClass(inputStream: InputStream): ByteArray
}

typealias ScanClassAction = (InputStream) -> ByteArray
