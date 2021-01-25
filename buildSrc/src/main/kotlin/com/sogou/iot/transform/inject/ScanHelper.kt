package com.sogou.iot.transform.inject

import com.sogou.iot.PluginHolder
import com.sogou.iot.TrLogger
import com.sogou.iot.Utils
import com.sogou.iot.replaceSlash2Dot
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.InputStream

/**
 * 文件名:ScanHelper
 * 创建者:baixuefei
 * 创建日期:2021/1/25 4:52 PM
 * 职责描述:
 */


object ScanHelper {

    fun doScanClass(sourceFile:File,destFile: File, inputStream: InputStream): ByteArray {
        //classReader
        var cr = ClassReader(inputStream)
        //classWriter
        var cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        //classAdapter
        var classAdapter =
            ScannClassVisitor(sourceFile,destFile, Opcodes.ASM7, cw)
        //classReader.accept()
        cr.accept(classAdapter, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }
}


class ScannClassVisitor(val sourceFile:File,val destFile: File, api: Int, classVisitor: ClassVisitor?) :
    ClassVisitor(api, classVisitor) {

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>
    ) {

        var mathedInterface = interfaces.filter {
            PluginHolder.getScanInterfaceType().equals(Utils.replaySeparator2Dot(it))
        }.size > 0

        var matchedInjectManager = false
        if (name.replaceSlash2Dot().equals(PluginHolder.getInjectManagerType())) {
            matchedInjectManager = true
            PluginHolder.injectMangetTargetFile = destFile
            PluginHolder.injectManagetSourceFile = sourceFile
        }

        if (mathedInterface) {
            PluginHolder.classsNameCollection.add(name)
        }

        TrLogger.d(
            "ScannClassVisitor === visit :${name},signature:$signature,superName:${superName},interfaces:${Utils.flattenStringArray(
                interfaces as Array<String>
            )},matched:${mathedInterface},matchedInjectManager:${matchedInjectManager},destFile:${destFile.absolutePath}"
        )
        super.visit(version, access, name, signature, superName, interfaces)
    }
}