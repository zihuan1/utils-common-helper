package com.zihuan.utils.cmhlibrary

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast


/**
 * 防止快速点击
 */
private var lastClickTime: Long = 0

@Synchronized
fun isFastClick(): Boolean {
    val time = System.currentTimeMillis()
    if (time - lastClickTime < 1000) {
        return true
    }
    lastClickTime = time
    return false
}


/**
 * 显示键盘
 */
fun showKeyBoard(view: EditText) {
    val imm = CommonContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    view.requestFocus()
    imm.showSoftInput(view, 0)
}

// 隐藏键盘
fun hideKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * 判断网络状态
 *
 * @return
 */
fun checkNetworkConnected(): Boolean {
    val mConnectivityManager = CommonContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val mNetworkInfo = mConnectivityManager.activeNetworkInfo
    if (mNetworkInfo != null) {
        return mNetworkInfo.isAvailable
    }
    return false
}
/**
 * 检测GPS是否打开
 *
 * @return
 */
fun checkGPSIsOpen(): Boolean {
    val locationManager = CommonContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}
/**
 * 状态栏高度
 *
 * @param activity
 * @return
 */
fun getStatusBarHeight(activity: Activity): Int {
    val resources = activity.resources
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}


//把dp转换成px
fun dip2px(dpValue: Float): Int {
    val scale = CommonContext.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

//把px转换成dp
fun px2dip(pxValue: Float): Int {
    val scale = CommonContext.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}


private var mToast: Toast? = null


/**
 * Toast
 *
 * @param argText
 */
fun ShowToast(argText: String) {
    val mainHandler = Handler(CommonContext.mainLooper)
    val myRunnable = Runnable {
        if (mToast != null) {
            mToast!!.cancel()
            mToast = null
        }
        mToast = Toast.makeText(CommonContext, argText, Toast.LENGTH_SHORT)
        mToast!!.setGravity(Gravity.CENTER, 0, 0)
        mToast!!.show()
    }
    mainHandler.post(myRunnable)
}
