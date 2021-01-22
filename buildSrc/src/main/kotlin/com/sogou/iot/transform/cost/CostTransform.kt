package com.sogou.iot.transform.cost

import com.sogou.iot.transform.BaseTransform
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.InputStream


/**
 * 文件名:CostTransform
 * 创建者:baixuefei
 * 创建日期:2021/1/20 11:23 AM
 * 职责描述: 函数耗时统计工具
 */


class CostTransform : BaseTransform() {
    override fun getName(): String {
        return "Cost"
    }

     override fun doScanClass(inputStream: InputStream): ByteArray {
        //classReader
        var cr = ClassReader(inputStream)
        //classWriter
        var cw = ClassWriter(cr, 0)
        //classAdapter
        var classAdapter =
            CostClassVisitor(Opcodes.ASM7, cw)
        //classReader.accept()
        cr.accept(classAdapter, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

}

