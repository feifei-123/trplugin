package com.sogou.iot.transform.cost

import com.sogou.iot.TrLogger
import org.objectweb.asm.MethodVisitor

/**
 * 文件名:ScanMethodVisitor
 * 创建者:baixuefei
 * 创建日期:2021/1/21 5:17 PM
 * 职责描述: TODO
 */



class ScanMethodVisitor(api: Int, methodVisitor: MethodVisitor?) :
    MethodVisitor(api, methodVisitor) {

    //访问的开始
    override fun visitCode() {
        TrLogger.d("visitCode")
        this.mv.visitFieldInsn(
            org.objectweb.asm.Opcodes.GETSTATIC,
            "java/lang/System",
            "out",
            "Ljava/io/PrintStream;"
        );
        this.mv.visitLdcInsn("------ enter haveATry");
        this.mv.visitMethodInsn(
            org.objectweb.asm.Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/String;)V",
            false
        );

//        this.mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
//        this.mv.visitVarInsn(LSTORE, 1);
        super.visitCode()
    }

    //访问方法局部变量的当前状态以及操作栈成员信息
    override fun visitFrame(
        type: Int,
        numLocal: Int,
        local: Array<out Any>?,
        numStack: Int,
        stack: Array<out Any>?
    ) {
        super.visitFrame(type, numLocal, local, numStack, stack)
    }

    //访问数值类型指令，opcode表示操作码指令,在这里opcode可以是Opcodes.BIPUSH,Opcodes.SIPUSH,Opcodes.NEWARRAY中一个；
    override fun visitInsn(opcode: Int) {
        TrLogger.d("visitCode:${opcode}")
        if (opcode == org.objectweb.asm.Opcodes.RETURN) { //方法返回前插入代码
            this.mv.visitFieldInsn(
                org.objectweb.asm.Opcodes.GETSTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;"
            );
            this.mv.visitLdcInsn("------ out haveATry");
            this.mv.visitMethodInsn(
                org.objectweb.asm.Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V",
                false
            );

        }
        super.visitInsn(opcode)

    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack, maxLocals)
    }

    override fun visitEnd() {
        super.visitEnd()
    }

}