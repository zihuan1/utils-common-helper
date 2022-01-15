package com.zihuan.demo

import android.util.Log
import com.google.gson.annotations.Expose

open class HelperCacheEntity {

    //最近一次完成时间 //上一次完成小助手脚本时间
    @Expose(serialize = true, deserialize = true)//序列化和反序列化都生效
    var last_finish_time = "0"

    @Expose(serialize = true, deserialize = true)
    var last_finished_botname = ""

    @Expose(serialize = true, deserialize = true)
    var finished_botnames: MutableList<FinishedBotNames>? = null

    override fun toString(): String {
        return "HelperCacheEntity(last_finish_time='$last_finish_time', last_finished_botname='$last_finished_botname', finished_botnames=$finished_botnames)"
    }


}