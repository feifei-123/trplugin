package com.sogou.iot.annotations;

import java.util.HashMap;

/**
 * 文件名:com.sogou.iot.trannotations.TimeCostCache
 * 创建者:baixuefei
 * 创建日期:2021/1/21 4:54 PM
 * 职责描述:
 */


public class TimeCostCache {
    public static HashMap<String, Long> times = new HashMap<>();

    public static void enterInMethod(String methodName) {
        times.put(methodName, System.currentTimeMillis());
    }

    public static void enterOutMethod(String methodName){
        long start = times.get(methodName);
        long end = System.currentTimeMillis();
        System.out.println("TimeCostCache --> "+methodName+" coast:"+(end-start)+"ms");
    }
}
