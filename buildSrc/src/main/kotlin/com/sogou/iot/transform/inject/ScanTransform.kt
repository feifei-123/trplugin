package com.sogou.iot.transform.inject

import com.android.build.api.transform.TransformInvocation
import com.sogou.iot.PluginHolder
import com.sogou.iot.transform.BaseTransform


import java.io.File
import java.io.InputStream

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

    override fun doScanClass(
        sourceFile: File,
        destFile: File,
        inputStream: InputStream
    ): ByteArray {
        return ScanHelper.doScanClass(sourceFile, destFile, inputStream)
    }

    override fun beforeTransform(transformInvocation: TransformInvocation?) {
        super.beforeTransform(transformInvocation)
    }

    override fun afeterTransform(transformInvocation: TransformInvocation?) {
        super.afeterTransform(transformInvocation)
        if (PluginHolder.injectMangetTargetFile != null && PluginHolder.injectMangetTargetFile?.exists()==true) {
            //找到了 被插入的类,此处插入操作
            if (PluginHolder.injectMangetTargetFile?.absolutePath?.endsWith(".jar") == true) {
                InjectHelper.inject2Jar(PluginHolder.injectMangetTargetFile!!)
            } else {
                InjectHelper.inject2ClassFile(PluginHolder.injectMangetTargetFile!!)
            }
        }
    }
}

