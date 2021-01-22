package com.sogou.iot.transform.inject

import com.sogou.iot.transform.BaseTransform
import com.sogou.iot.transform.cost.CostClassVisitor
import jdk.internal.org.objectweb.asm.Opcodes
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.InputStream

/**
 * 文件名:InjectTransform
 * 创建者:baixuefei
 * 创建日期:2021/1/21 9:53 PM
 * 职责描述:
 */


class InjectTransform:BaseTransform() {



    override fun getName(): String {
        return "inject"
    }

   override fun doScanClass(inputStream: InputStream): ByteArray {
        //classReader
        var cr = ClassReader(inputStream)
        //classWriter
        var cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        //classAdapter
        var classAdapter =
            CostClassVisitor(Opcodes.ASM5, cw)
        //classReader.accept()
        cr.accept(classAdapter, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

}