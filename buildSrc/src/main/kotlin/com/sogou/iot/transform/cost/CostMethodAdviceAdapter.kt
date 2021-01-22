package com.sogou.iot.transform.cost

import com.sogou.iot.TrLogger
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

/**
 * 文件名:CostAdviceAdapter
 * 创建者:baixuefei
 * 创建日期:2021/1/21 5:08 PM
 * 职责描述:
 */


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