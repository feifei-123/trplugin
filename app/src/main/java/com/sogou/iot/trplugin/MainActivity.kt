package com.sogou.iot.trplugin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sogou.iot.annotations.Cost
import com.sogou.iot.annotations.TimeCostCache
import java.io.File

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

fun main(args:Array<String>){

    System.out.println("feifei---test asm ---- ")
//    var inputFile = File("/Users/feifei/Desktop/TM/Demo/TrPlugin/app/build/intermediates/transforms/Scan/debug/33/com/sosou/iot/trplugin/ComponentManager.class")
//    var outputFile = File("/Users/feifei/Desktop/TM/Demo/TrPlugin/app/build/intermediates/transforms/inject/debug/33/com/sosou/iot/trplugin/ComponentManager.class")
//    TestAsm().scanClass(inputFile,outputFile)

    Thread.sleep(10000)
    System.out.println("feifei---test asm ----2222 ")

}
