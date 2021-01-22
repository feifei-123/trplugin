package com.sogou.iot

import java.io.File

/**
 * 文件名:Utils
 * 创建者:baixuefei
 * 创建日期:2021/1/20 2:29 PM
 * 职责描述: TODO
 */


object Utils {
    fun getClassNameForFile(rootPath: String, file: File): String {
        var className = file.absolutePath.replace(rootPath, "")
        if (File.separator == "/") {
            className = className.replace("\\\\", "/")
        }
        className = className.replace(File.separator, ".")
        return className
    }

    fun flattenStringArray(arr: Array<String>): String {
        val buffer = StringBuffer()
        buffer.append("[ ")
        arr.forEach {
            buffer.append(it).append(",")
        }
        buffer.removeSuffix(",")
        buffer.append(" ]")
        return buffer.toString()
    }
}