package com.zihuan.demo

import com.google.gson.annotations.Expose

/**
 * 五处用心缓存类
 */
class WCYXEntity : HelperCacheEntity() {

    @Expose(serialize = false, deserialize = true)
    var clock_times = ""  //打卡次数，只要心理积累，就会清零，且表示的是当前from_page的次数。

    @Expose(serialize = false, deserialize = true)
    var xinli_question_count = "0" // 心理检测的问题数

    @Expose(serialize = false, deserialize = true)
    var commonXinliName = ""
    override fun toString(): String {
        return "WCYXEntity(clock_times='$clock_times', xinli_question_count='$xinli_question_count', commonXinliName='$commonXinliName')"
    }


}