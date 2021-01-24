package com.sogou.iot.transform.inject

import com.sogou.iot.*
import com.sogou.iot.transform.BaseTransform
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.commons.AdviceAdapter
import java.io.InputStream

/**
 * 文件名:InjectTransform
 * 创建者:baixuefei
 * 创建日期:2021/1/21 9:53 PM
 * 职责描述:
 */


class InjectTransform : BaseTransform() {

    override fun getName(): String {
        return "inject"
    }

    override fun doScanClass(inputStream: InputStream): ByteArray {
        var cr = ClassReader(inputStream)
        var cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        var classAdapter =
            InjectClassVisitor(Opcodes.ASM7, cw)
        cr.accept(classAdapter, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
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
//            else {
//                return null
//            }
            return InjectMethodVisitor(ASM7, mv, access, name, descriptor)
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
                ACC_PUBLIC,
                "comoments",
                "Ljava/util/ArrayList;",
                "Ljava/util/ArrayList<${it}>;",
                null
            );
            fieldVisitor.visitEnd();
        }

        //初始化属性
        methodVisitor?.visitVarInsn(ALOAD, 0);
        methodVisitor?.visitTypeInsn(NEW, "java/util/ArrayList");
        methodVisitor?.visitInsn(DUP);
        methodVisitor?.visitMethodInsn(
            INVOKESPECIAL,
            "java/util/ArrayList",
            "<init>",
            "()V",
            false
        );
        methodVisitor?.visitFieldInsn(
            PUTFIELD,
            "com/sogou/iot/trplugin/ComponentManager",
            "comoments",
            "Ljava/util/ArrayList;"
        );
    }

    fun generateMethod() {
        var methodVisitor = cv.visitMethod(
            ACC_PUBLIC or ACC_SYNCHRONIZED,
            "initComponet11",
            "()V",
            null,
            null
        )

        methodVisitor.visitCode()
        //...此处增加方法的具体实现
        methodVisitor.visitInsn(RETURN)
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
        );
    }
}