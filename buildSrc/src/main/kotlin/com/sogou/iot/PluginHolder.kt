package com.sogou.iot

import org.gradle.api.Project
import java.io.File

/**
 * 文件名:PluginHolder
 * 创建者:baixuefei
 * 创建日期:2021/1/22 10:18 AM
 * 职责描述:
 */


open class ComponentExtension {
    //待搜集的接口类
    var matchInterfaceType: String = ""

    //Container容器类
    var matchInjectManagerType: String = ""

    //容器类的
    var matchInjectManagerInjectMethod: String = ""
}

object PluginHolder {
    const val componentExt = "componentExt"
    private var project: Project? = null

    fun setProject(project: Project) {
        this.project = project
    }

    fun getScanInterfaceType(): String? {
        return (project?.extensions?.getByName(componentExt) as ComponentExtension).matchInterfaceType
    }

    fun getInjectManagerType(): String? {
        return (project?.extensions?.getByName(componentExt) as ComponentExtension).matchInjectManagerType
    }

    fun getInjectManagetTypeWithoutPackage():String{
        var arr = getInjectManagerType()?.split('.')
        var result = ""
        arr?.let {
            result = it[it.size-1]
        }
        return result
    }

    fun getInjectManagetInjectMethod(): String? {
        return (project?.extensions?.getByName(componentExt) as ComponentExtension).matchInjectManagerInjectMethod
    }

    var classsNameCollection = mutableSetOf<String>()
    var injectMangetTargetFile: File? = null
    var injectManagetSourceFile:File? = null
}

