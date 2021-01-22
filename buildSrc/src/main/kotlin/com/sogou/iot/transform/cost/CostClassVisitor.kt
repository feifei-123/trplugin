package com.sogou.iot.transform.cost

import com.sogou.iot.TrLogger
import com.sogou.iot.Utils
import jdk.internal.org.objectweb.asm.Opcodes
import org.objectweb.asm.*

/**
 * 文件名:CostClassVisitor
 * 创建者:baixuefei
 * 创建日期:2021/1/21 3:12 PM
 * 职责描述: 计算耗时
 */


class CostClassVisitor(api: Int, classVisitor: ClassVisitor?) : ClassVisitor(api, classVisitor) {


    //访问ClassHeader 类头
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        TrLogger.d(
            "ScanClassVisitor === visit :${name},signature:$signature,superName:${superName},interfaces:${Utils.flattenStringArray(
                interfaces as Array<String>
            )}"
        )

        super.visit(version, access, name, signature, superName, interfaces)
    }

    //访问方法
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
//        TrLogger.d("ScanClassVisitor === visitMethod :${name},descriptor:$descriptor")
        val mv = cv.visitMethod(access, name, descriptor, signature, exceptions)
        return CostMethodAdviceAdapter(Opcodes.ASM5, mv, access, name, descriptor)
//        return mv
    }

    //访问属性
    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
//        TrLogger.d("ScanClassVisitor=== visitField :${name},descriptor:$descriptor")
        return super.visitField(access, name, descriptor, signature, value)
    }


    //访问类 - 结束
    override fun visitEnd() {
//        TrLogger.d("ScanClassVisitor=== visitEnd ")
        super.visitEnd()
    }
}

