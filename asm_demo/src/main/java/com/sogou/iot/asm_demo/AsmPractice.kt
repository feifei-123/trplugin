package com.sogou.iot.asm_demo

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.util.TraceClassVisitor
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.PrintWriter


class AsmPractice {


    //移除原有类中的一个方法
    fun scanClassRemoveMethod(file: File, out: File) {
        var inputStream = FileInputStream(file)
        var outputStream = FileOutputStream(out)

        //构造ClassReader对象
        var cr = ClassReader(inputStream)
        //构造ClassWriter对象
        var cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        //构造ClassAdapter对象,指定其传递链的下一级为ClassWriter
        var classAdapter =
            RemoveMethodClassVisitor(Opcodes.ASM7, cw)
        //ClassReader读取类的信息,指定读取到的类信息传递到ClassAdapter
        cr.accept(classAdapter, ClassReader.EXPAND_FRAMES)
        //最终的类转换成ByteArray
        var newClassBytes = cw.toByteArray()
        outputStream.write(newClassBytes)
        inputStream.close()
        outputStream.close()
    }

    //移除一个属性
    fun scanClassRemoveFiled(file: File, out: File) {
        var inputStream = FileInputStream(file)
        var outputStream = FileOutputStream(out)
        var cr = ClassReader(inputStream)
        var cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        var classAdapter =
            RemoveFiledClassVisitor(Opcodes.ASM7, cw)
        cr.accept(classAdapter, ClassReader.EXPAND_FRAMES)
        var newClassBytes = cw.toByteArray()
        outputStream.write(newClassBytes)
        inputStream.close()
        outputStream.close()
    }

    //新增一个属性
    fun scanClassAddField(file: File, out: File) {
        var inputStream = FileInputStream(file)
        var outputStream = FileOutputStream(out)
        var cr = ClassReader(inputStream)
        var cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        var classAdapter =
            AddFiledClassVisitor(Opcodes.ASM7, cw)
        cr.accept(classAdapter, ClassReader.EXPAND_FRAMES)
        var newClassBytes = cw.toByteArray()
        outputStream.write(newClassBytes)
        inputStream.close()
        outputStream.close()
    }

    //新增方法
    fun scanClassAddMethod(file: File, out: File) {
        var inputStream = FileInputStream(file)
        var outputStream = FileOutputStream(out)
        var cr = ClassReader(inputStream)
        var cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        var classAdapter =
            AddMethodClassVisitor(Opcodes.ASM7, cw)
        cr.accept(classAdapter, ClassReader.EXPAND_FRAMES)
        var newClassBytes = cw.toByteArray()
        outputStream.write(newClassBytes)
        inputStream.close()
        outputStream.close()
    }

    fun scanClassModifyMethod(file: File, out: File) {
        var inputStream = FileInputStream(file)
        var outputStream = FileOutputStream(out)
        var cr = ClassReader(inputStream)
        var cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        var classAdapter =
            ModifyMethodClassVisitor(Opcodes.ASM7, cw)
        cr.accept(classAdapter, ClassReader.EXPAND_FRAMES)
        var newClassBytes = cw.toByteArray()

        outputStream.write(newClassBytes)
        inputStream.close()
        outputStream.close()

    }

    //TraceClassVisitor
    fun scanClassTraceClassVisitor(file: File, out: File) {
        var inputStream = FileInputStream(file)
        var outputStream = FileOutputStream(out)
        var cr = ClassReader(inputStream)
        var cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        val traceClassVisitor = TraceClassVisitor(cw, PrintWriter(System.out))
        cr.accept(traceClassVisitor, ClassReader.EXPAND_FRAMES)
        var newClassBytes = cw.toByteArray()
        outputStream.write(newClassBytes)
        inputStream.close()
        outputStream.close()
    }

    //生成一个新的类
    fun generateClass(out: File) {
        var outputStream = FileOutputStream(out)

        val cw = ClassWriter(0)
        var fv: FieldVisitor
        var mv: MethodVisitor
        var av0: AnnotationVisitor

        cw.visit(
            V1_7,
            ACC_PUBLIC + ACC_SUPER,
            "com/sogou/iot/asm_demo/Comparable",
            null,
            "java/lang/Object",
            null
        )

        cw.visitSource("Comparable.java", null)


        kotlin.run {
            fv = cw.visitField(0, "LESS", "I", null, null)
            fv.visitEnd()
        }

        kotlin.run {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
            mv.visitCode()

            val l0 = Label()
            mv.visitLabel(l0)
            mv.visitLineNumber(11, l0)
            mv.visitVarInsn(ALOAD, 0)
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
            val l1 = Label()
            mv.visitLabel(l1)
            mv.visitLineNumber(12, l1)
            mv.visitVarInsn(ALOAD, 0)
            mv.visitInsn(ICONST_M1)
            mv.visitFieldInsn(PUTFIELD, "com/sogou/iot/asm_demo/Comparable", "LESS", "I")
            mv.visitInsn(RETURN)
            val l2 = Label()
            mv.visitLabel(l2)
            mv.visitLocalVariable("this", "Lcom/sogou/iot/asm_demo/Comparable;", null, l0, l2, 0)
            mv.visitMaxs(2, 1)
            mv.visitEnd()
        }



        kotlin.run {
            mv = cw.visitMethod(0, "compareTo", "(Ljava/lang/Object;)I", null, null)
            mv.visitCode()
            val l0 = Label()
            mv.visitLabel(l0)
            mv.visitLineNumber(14, l0)
            mv.visitInsn(ICONST_M1)
            mv.visitInsn(IRETURN)
            val l1 = Label()
            mv.visitLabel(l1)
            mv.visitLocalVariable("this", "Lcom/sogou/iot/asm_demo/Comparable;", null, l0, l1, 0)
            mv.visitLocalVariable("o", "Ljava/lang/Object;", null, l0, l1, 1)
            mv.visitMaxs(1, 2)
            mv.visitEnd()
        }

        cw.visitEnd()

        val bytes = cw.toByteArray()
        outputStream.write(bytes)
        outputStream.close()

    }
}

class RemoveMethodClassVisitor(api: Int, classVisitor: ClassVisitor?) :
    ClassVisitor(api, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        System.out.println("visitMethod --- :${name},descriptor:${descriptor}")
        if (name?.equals("toDeleteMethod") == true && descriptor.equals("()V") == true) {

            return null
        } else {
            //删除一个方关键的两点:
            // （1）不调用 cv.visitMethod(),调用cv.visitMethod()会产生一个MethodVisitor 生成对应的方法
            //  (2) 不将methodVisitor返回
            var mv = cv.visitMethod(access, name, descriptor, signature, exceptions)
            return mv
        }
    }

    override fun visitSource(source: String?, debug: String?) {
        System.out.println("visitSource --- :${source} ,debug:${debug}")
        super.visitSource(source, debug)
    }

}

class RemoveFiledClassVisitor(api: Int, classVisitor: ClassVisitor?) :
    ClassVisitor(api, classVisitor) {
    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor? {
        System.out.println("visitField --- access:${access}:${name},descriptor:${descriptor},signature:${signature},value:${value}")
        if (name.equals("toDeleteFiled") && descriptor.equals("Ljava/lang/String;")) {
            //删除一个属性,只需要特定属性 visitField 返回null即可
            return null
        } else {
            return cv.visitField(access, name, descriptor, signature, value)
        }
    }
}


class AddFiledClassVisitor(api: Int, classVisitor: ClassVisitor?) :
    ClassVisitor(api, classVisitor) {


    val toAddFiledName = "addedFileld"
    val toAddFiledDes = "Ljava/lang/String;"
    var conflict: Boolean = false
    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        if (name.equals(toAddFiledName) && descriptor.equals(toAddFiledDes)) {
            conflict = true
        }
        return super.visitField(access, name, descriptor, signature, value)

    }

    //增加一个属性注意两点:
    //(1)新增的属性和类中已有的属性不能冲突(如重名等)
    //(2)理论上在访问该类的任何时机都可以新增特定属性,但是考虑到(1)新增属性不能和现有属性冲突,所有最好在visitEnd中 排除属性冲突后,执行新增属性的任务
    //(3)新增属性只需要调用 cv.visitField()和fv.visitEnd() 就可完整新增一个属性
    override fun visitEnd() {

        System.out.println("visitEnd ,conflict:${conflict},${Type.getType(Array<String>::class.java)}")
        if (conflict == false) {
            //生成实例属性
            val filedVisitor = cv.visitField(
                Opcodes.ACC_PUBLIC,
                "addedFileld",
                Type.getType(Array<String>::class.java).descriptor,
                null,
                null
            )
            //生成静态属性
            //val staticfiledVisitor = cv.visitField((Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC),"addedFileld","Ljava/lang/String;",null,null)
            filedVisitor.visitEnd()
        }
        super.visitEnd()
    }
}

//增加方法
class AddMethodClassVisitor(api: Int, classVisitor: ClassVisitor?) :
    ClassVisitor(api, classVisitor) {

    val toAddMethodName = "addedMethod"
    val toAddMethodDescriptor = "()V"
    var confict = false
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (name.equals(toAddMethodName) && descriptor.equals(toAddMethodDescriptor)) {
            confict = true
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    override fun visitEnd() {
        if (confict == false) {
            var methodVisitor = cv.visitMethod(
                Opcodes.ACC_PUBLIC or Opcodes.ACC_SYNCHRONIZED,
                toAddMethodName,
                toAddMethodDescriptor,
                null,
                null
            )

            methodVisitor.visitCode()
            //...此处增加方法的具体实现
            methodVisitor.visitInsn(Opcodes.RETURN)
            methodVisitor.visitMaxs(0, 1)
            methodVisitor.visitEnd()
        }
        super.visitEnd()
    }
}

class ModifyMethodClassVisitor(api: Int, classVisitor: ClassVisitor?) :
    ClassVisitor(api, classVisitor) {
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (name.equals("haveATry")) {
            val mv = cv.visitMethod(access, name, descriptor, signature, exceptions)
            return ScanMethodvisitor(api, mv, access, name, descriptor)
        } else {
            return cv.visitMethod(access, name, descriptor, signature, exceptions)
        }
    }
}

class ScanMethodvisitor(
    api: Int,
    methodVisitor: MethodVisitor?,
    access: Int,
    name: String?,
    descriptor: String?
) : AdviceAdapter(api, methodVisitor, access, name, descriptor) {

    override fun onMethodEnter() {


        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        //注意这里新增的局部变量 不能和方法原有的局部变量有冲突
        mv.visitVarInsn(ASTORE, 10)

        super.onMethodEnter()
    }

    override fun onMethodExit(opcode: Int) {

        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        mv.visitVarInsn(ASTORE, 11)

        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitLdcInsn("calucate cost:");
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
            false
        );
        mv.visitVarInsn(ALOAD, 11);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
        mv.visitVarInsn(ALOAD, 10);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
        mv.visitInsn(LSUB);
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(J)Ljava/lang/StringBuilder;",
            false
        );
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "toString",
            "()Ljava/lang/String;",
            false
        );
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/String;)V",
            false
        );
        super.onMethodExit(opcode)
    }
}


fun main(args: Array<String>) {

    System.out.println("feifei---run asm start ---- ")
    var inputPath = "./asm_demo/src/main/source/com/sogou/iot/trplugin/"
    var outputPath = "./asm_demo/src/main/target/com/sogou/iot/trplugin/"
    var inputFile = File("${inputPath}ComponentManager.class")
    var outputFile = File("${outputPath}ComponentManager.class")

    //移除一个方法
//    AsmPractice().scanClassRemoveMethod(inputFile,outputFile)
    //移除一个属性
    //AsmPractice().scanClassRemoveFiled(inputFile,outputFile)
    //新增一个属性
//    AsmPractice().scanClassAddField(inputFile,outputFile)
    //新增方法
//    AsmPractice().scanClassAddMethod(inputFile,outputFile)

    //修改一个方法

    AsmPractice().scanClassModifyMethod(
        File("${inputPath}TestCost.class"),
        File("${outputPath}TestCost.class")
    )

    //生成一个类
    //AsmPractice().generateClass(outputFile)

    System.out.println("feifei---run asm finish---- ")

}
