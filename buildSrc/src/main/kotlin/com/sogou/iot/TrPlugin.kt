package com.sogou.iot

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.sogou.iot.transform.cost.CostTransform
import com.sogou.iot.transform.inject.InjectTransform
import com.sogou.iot.transform.inject.ScanTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 文件名:TrPlugin
 * 创建者:baixuefei
 * 创建日期:2021/1/20 11:10 AM
 * 职责描述: 自定义插件入口
 */


class TrPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        var isApp =
            project.plugins.hasPlugin(AppPlugin::class.java) //是否引入了com.android.application 插件
        TrLogger.setLogger(project.logger)
        if (isApp) {

            PluginHolder.setProject(project)

            //注册Transform
            TrLogger.e("register CostTransform")

            project.extensions.create(PluginHolder.componentExt, ComponentExtension::class.java)

            project.extensions.getByType(AppExtension::class.java)?.apply {
//                registerTransform(CostTransform())
                registerTransform(ScanTransform()) //扫描,收集类信息
                registerTransform(InjectTransform())//处理手机到的类信息
            }

        }
    }

}
