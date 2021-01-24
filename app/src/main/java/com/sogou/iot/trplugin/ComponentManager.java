package com.sogou.iot.trplugin;

import android.util.Log;

import java.util.ArrayList;

/**
 * 文件名:ComponentContrainer
 * 创建者:baixuefei
 * 创建日期:2021/1/21 9:18 PM
 * 职责描述:
 */


//插件管理器
public class ComponentManager {

    public synchronized void initComponet() {
    }

    public void toDeleteMethod() {

    }

    public void calculate(){
        Long start = System.currentTimeMillis();
        int i = 10;
        int j = 100+i;
        Long end = System.currentTimeMillis();
        System.out.println("calucate cost:"+(end-start));

    }
    public String toDeleteFiled;
}
