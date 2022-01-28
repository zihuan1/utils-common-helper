package com.zihuan.demo

import com.google.gson.annotations.Expose

class FinishedBotNames {

    @Expose(serialize = true, deserialize = true)
    var botname = ""

    @Expose(serialize = true, deserialize = true)
    var last_finish_time = ""

    @Expose(serialize = true, deserialize = true)
    var finished_times = ""
}