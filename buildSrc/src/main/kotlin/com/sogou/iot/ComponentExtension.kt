package com.sogou.iot

/**
 * 文件名:ComponentExtension
 * 创建者:baixuefei
 * 创建日期:2021/1/25 7:33 PM
 * 职责描述: TODO
 */


open class ComponentExtension {
    //待搜集的接口类
    var matchInterfaceType: String = ""

    //Container容器类
    var matchInjectManagerType: String = ""

    //容器类的
    var matchInjectManagerInjectMethod: String = ""

    var openLog:Boolean = false

    var logLevel:Int = TrLogger.LogLevelDebug
}