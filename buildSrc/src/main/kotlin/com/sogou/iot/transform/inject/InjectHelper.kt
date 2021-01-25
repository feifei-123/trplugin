package com.sogou.iot.transform.inject

import com.sogou.iot.*
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * 文件名:InsertHelper
 * 创建者:baixuefei
 * 创建日期:2021/1/25 5:16 PM
 * 职责描述:
 */


object InjectHelper {

    fun doGenerateCode2Manager(inputStream: InputStream): ByteArray {

        var cr = ClassReader(inputStream)
        var cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        var classAdapter =
            InjectClassVisitor(Opcodes.ASM7, cw)
        cr.accept(classAdapter, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

    fun isInjectManagerClass(entryName: String): Boolean {
        var shouldInject = false
        if (entryName.isEmpty() || !entryName.endsWith(".class")) {
            shouldInject =  false
        }

        if (entryName.contains(PluginHolder.getInjectManagetTypeWithoutPackage())) {
            shouldInject =  true
        }

        TrLogger.d("isInjectManagerClass:${shouldInject},entryName:${entryName},InjectManagerType:${PluginHolder.getInjectManagetTypeWithoutPackage()}")

        return shouldInject
    }


    fun inject2Jar(inputJar: File) {
        var srcJar = JarFile(inputJar)

        //临时输出文件
        var outputJar = File(inputJar.getParent(), inputJar.name + ".opt")
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
                TrLogger.d("inject2Jar entryName ===>:${entryName}")
                //输出文件
                var outputZipEntry = ZipEntry(entryName)
                outputJarStream.putNextEntry(outputZipEntry)

                //目录不需要处理
                if (isInjectManagerClass(inputJarEntry.name)) { //需要扫描
                    val newClassBytes = doGenerateCode2Manager(inputJarStream)
                    outputJarStream.write(newClassBytes)
                } else {
                    outputJarStream.write(IOUtils.toByteArray(inputJarStream))
                }

                outputJarStream.closeEntry()

            }
        }

        outputJarStream.close()
        srcJar.close()

        if (inputJar.exists()) {
            inputJar.delete()
        }
        outputJar.renameTo(inputJar)

        PluginHolder.injectManagetSourceFile?.let {
         if(it.exists()) it.delete()
            FileUtils.copyFile(inputJar,it)
        }
    }

    fun inject2ClassFile(classfile: File) {
        if (isInjectManagerClass(classfile.name)) {
            var optClass = File(classfile.getParent(), classfile.name + ".opt")
            val inputStream = FileInputStream(classfile)
            val outputStream = FileOutputStream(optClass);
            var bytes = doGenerateCode2Manager(inputStream)
            outputStream.write(bytes)
            inputStream.close()
            outputStream.close()
            if (classfile.exists()) {
                classfile.delete()
            }
            optClass.renameTo(classfile)

            PluginHolder.injectManagetSourceFile?.let {
                if(it.exists()) it.delete()
                FileUtils.copyFile(classfile,it)
            }
        }
    }
}

class InjectClassVisitor(api: Int, classVisitor: ClassVisitor?) : ClassVisitor(api, classVisitor) {

    var injectManagetMatched = false
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {

        if (name?.replaceSlash2Dot().equals(PluginHolder.getInjectManagerType())) {
            injectManagetMatched = true
        }
        TrLogger.d("InjectClassVisitor visit :${name},injectManagetMatched:${injectManagetMatched}")
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {

        TrLogger.d("InjectClassVisitor visitMethod:${name}")
        val mv = cv.visitMethod(access, name, descriptor, signature, exceptions)
        if (injectManagetMatched) {
            if (name.equals("<init>")) {
                generateField(mv)
            }
            return InjectMethodVisitor(Opcodes.ASM7, mv, access, name, descriptor)
        } else {
            return mv
        }
    }

    override fun visitEnd() {
        if (injectManagetMatched) {
            generateMethod()
        }
        super.visitEnd()
    }

    //生成一个属性,并且在某个方法MethodVisitor中,初始化属性
    fun generateField(methodVisitor: MethodVisitor) {
        //生成属性
        val fieldSignature = PluginHolder.getScanInterfaceType()?.toSignature()
        fieldSignature?.let {
            var fieldVisitor = cv.visitField(
                Opcodes.ACC_PUBLIC,
                "comoments",
                "Ljava/util/ArrayList;",
                "Ljava/util/ArrayList<${it}>;",
                null
            );
            fieldVisitor.visitEnd();
        }
    }

    fun generateMethod() {
        var methodVisitor = cv.visitMethod(
            Opcodes.ACC_PUBLIC or Opcodes.ACC_SYNCHRONIZED,
            "initComponet11",
            "()V",
            null,
            null
        )

        methodVisitor.visitCode()
        //...此处增加方法的具体实现
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitMaxs(0, 1)
        methodVisitor.visitEnd()
    }
}

class InjectMethodVisitor(
    api: Int,
    methodVisitor: MethodVisitor?,
    access: Int,
    name: String?,
    descriptor: String?
) : AdviceAdapter(api, methodVisitor, access, name, descriptor) {

    var insertMehodMatched = false
    var isConstructor = false

    init {
        if (this.name.equals(PluginHolder.getInjectManagetInjectMethod())) {
            insertMehodMatched = true
            PluginHolder.classsNameCollection.forEach {
                TrLogger.d("InjectMethodVisitor classsNameCollection ---- :${it}")
            }
        } else if (this.name.equals("<init>")) {
            isConstructor = true
        }
        TrLogger.d("InjectMethodVisitor:${name},insertMehodMatched:${insertMehodMatched}")
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        if (insertMehodMatched) {
            modifyMethod()
        } else if (isConstructor) {
            initFiled()
        }
    }

    fun modifyMethod() {

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            "com/sogou/iot/trplugin/ComponentManager",
            "components",
            "Ljava/util/ArrayList;"
        )
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "clear", "()V", false);

        PluginHolder.classsNameCollection.forEach {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(
                GETFIELD,
                "com/sogou/iot/trplugin/ComponentManager",
                "components",
                "Ljava/util/ArrayList;"
            );
            mv.visitTypeInsn(NEW, it.replaceDot2Slash());
            mv.visitInsn(DUP)
            mv.visitMethodInsn(
                INVOKESPECIAL,
                "com/sogou/iot/trplugin/TransComponent",
                "<init>",
                "()V",
                false
            );
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "java/util/ArrayList",
                "add",
                "(Ljava/lang/Object;)Z",
                false
            );
            mv.visitInsn(POP)
        }
    }

    fun initFiled() {
        //初始化属性
        mv?.visitVarInsn(Opcodes.ALOAD, 0);
        mv?.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
        mv?.visitInsn(Opcodes.DUP);
        mv?.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "java/util/ArrayList",
            "<init>",
            "()V",
            false
        );
        mv?.visitFieldInsn(
            Opcodes.PUTFIELD,
            "com/sogou/iot/trplugin/ComponentManager",
            "comoments",
            "Ljava/util/ArrayList;"
        )
    }
}