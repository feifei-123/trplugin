package com.sogou.iot.trplugin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sogou.iot.annotations.Cost
import com.sogou.iot.annotations.TimeCostCache

class MainActivity : AppCompatActivity() {
    @Cost
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @Cost
    override fun onResume() {
        super.onResume()
    }
}