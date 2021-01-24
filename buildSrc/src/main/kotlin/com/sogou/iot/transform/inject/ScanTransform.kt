package com.sogou.iot.transform.inject

import com.sogou.iot.PluginHolder
import com.sogou.iot.TrLogger
import com.sogou.iot.TrPlugin
import com.sogou.iot.Utils
import com.sogou.iot.transform.BaseTransform
import com.sogou.iot.transform.cost.CostClassVisitor


import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.InputStream
import javax.xml.crypto.dsig.Transform

/**
 * 文件名:ScanTransform
 * 创建者:baixuefei
 * 创建日期:2021/1/22 10:33 AM
 * 职责描述:
 */

class ScanTransform : BaseTransform() {


    override fun getName(): String {
        return "Scan"
    }

    override fun doScanClass(inputStream: InputStream): ByteArray {
        //classReader
        var cr = ClassReader(inputStream)
        //classWriter
        var cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        //classAdapter
        var classAdapter =
            ScannClassVisitor(Opcodes.ASM7, cw)
        //classReader.accept()
        cr.accept(classAdapter, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

}

class ScannClassVisitor(api: Int, classVisitor: ClassVisitor?) : ClassVisitor(api, classVisitor) {

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

        if (mathedInterface) {
            PluginHolder.classsNameCollection.add(name)
        }

        TrLogger.d(
            "ScannClassVisitor === visit :${name},signature:$signature,superName:${superName},interfaces:${Utils.flattenStringArray(
                interfaces as Array<String>
            )},matched:${mathedInterface}"
        )
        super.visit(version, access, name, signature, superName, interfaces)
    }
}