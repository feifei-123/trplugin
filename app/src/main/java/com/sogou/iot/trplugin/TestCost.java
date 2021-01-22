package com.sogou.iot.trplugin;

import com.sogou.iot.annotations.Cost;

/**
 * 文件名:TestCost
 * 创建者:baixuefei
 * 创建日期:2021/1/21 9:52 AM
 * 职责描述:
 */


class TestCost {

    @Cost
    public void haveATry() {
        int i = 0;
        int j = i + 10;
        System.out.println("haveATry j:"+j);

    }
}
