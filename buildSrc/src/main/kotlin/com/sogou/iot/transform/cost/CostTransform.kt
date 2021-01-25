package com.sogou.iot.transform.cost

import com.sogou.iot.TrLogger
import com.sogou.iot.Utils
import com.sogou.iot.transform.BaseTransform
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter
import java.io.File
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

     override fun doScanClass(sourceFile:File,destFile: File, inputStream: InputStream): ByteArray {
        //classReader
        var cr = ClassReader(inputStream)
        //classWriter
        var cw = ClassWriter(cr, 0)
        //classAdapter
        var classAdapter = CostClassVisitor(Opcodes.ASM7, cw)
        //classReader.accept()
        cr.accept(classAdapter, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

}

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

    }

    //访问属性
    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        return super.visitField(access, name, descriptor, signature, value)
    }


    //访问类 - 结束
    override fun visitEnd() {
        super.visitEnd()
    }
}


class CostMethodAdviceAdapter(
    api: Int,
    methodVisitor: MethodVisitor?,
    access: Int,
    name: String?,
    descriptor: String?
) : AdviceAdapter(api, methodVisitor, access, name, descriptor) {
    var methodName: String? = null
    var inject = false

    init {
        methodName = name

    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        TrLogger.d("CostMethodAdviceAdapter visitAnnotation:${descriptor}")
        if (descriptor?.equals("Lcom/sogou/iot/annotations/Cost;") == true) {
            inject = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun onMethodEnter() {
        if(inject){
            TrLogger.d("CostMethodAdviceAdapter onMethodEnter:${inject},methodName:${methodName}")
            mv.visitLdcInsn(methodName)
            mv.visitMethodInsn(INVOKESTATIC, "com/sogou/iot/annotations/TimeCostCache", "enterInMethod", "(Ljava/lang/String;)V", false);
        }
    }

    override fun onMethodExit(opcode: Int) {
        if(inject){
            TrLogger.d("CostMethodAdviceAdapter onMethodExit:${inject},methodName:${methodName}")
            mv.visitLdcInsn(methodName);
            mv.visitMethodInsn(INVOKESTATIC, "com/sogou/iot/annotations/TimeCostCache", "enterOutMethod", "(Ljava/lang/String;)V", false);
        }
    }
}