package com.sogou.iot

import org.gradle.api.logging.Logger

/**
 * 文件名:TrLogger
 * 创建者:baixuefei
 * 创建日期:2021/1/20 11:16 AM
 * 职责描述: 日志打印工具类
 */


object TrLogger {
    private var innerLogger: Logger? = null
    val tag:String = "TrPlugin >>> "
    fun setLogger(logger: Logger) {
        innerLogger = logger
    }

    fun i(info: String) {
        var msg = tag+info
        innerLogger?.let {
            it.info(msg)
        }
        println(msg)
    }

    fun d(info:String){
        var msg = tag+info
        innerLogger?.let {
            it.info(msg)
        }
        println(msg)
    }

    fun e(info:String){
        var msg = tag+info
        innerLogger?.let {
            it.info(msg)
        }
        println(msg)
    }

    private fun println(msg:String){
        kotlin.io.println(msg)
    }

}