package com.zihuan.utils.cmhlibrary

import android.content.Context
import android.content.ContextWrapper


private lateinit var mContext: Context
//是否异步提交preference
  var preferenceAsyn = true

object CommonHelperCreate {
    @JvmStatic
    fun setContext(context: Context) {
        mContext = context
    }

}

object CommonContext : ContextWrapper(mContext)