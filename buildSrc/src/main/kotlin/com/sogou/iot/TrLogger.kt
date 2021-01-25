package com.sogou.iot

import org.gradle.api.logging.Logger

/**
 * 文件名:TrLogger
 * 创建者:baixuefei
 * 创建日期:2021/1/20 11:16 AM
 * 职责描述: 日志打印工具类
 */


object TrLogger {

    const val LogLevelException = 1
    const val LogLevelDebug = 2
    const val LogLevelVerbose = 3
    const val LogLevelInfo = 4

    private var innerLogger: Logger? = null
    val tag:String = "TrPlugin >>> "
    var openLog = true
    var logLevel = LogLevelInfo




    fun setLogger(logger: Logger) {
        innerLogger = logger
    }

    fun i(info: String) {
        if(!openLog) return
        if(logLevel < LogLevelInfo) return
        println(makeTag(info))
    }

    fun d(info:String){
        if(!openLog) return
        if(logLevel < LogLevelDebug) return
        println(makeTag(info))
    }

    fun e(info:String){
        if(!openLog) return
        if(logLevel < LogLevelException) return

        println(makeTag(info))
    }

    private fun println(msg:String){
        innerLogger?.let {
            it.info(msg)
        }
        kotlin.io.println(msg)
    }

    private fun makeTag(msg:String):String{
        return tag+msg
    }
}