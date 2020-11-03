package com.zihuan.utils.cmhlibrary

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.WindowManager
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
fun EditText.showKeyBoard(): EditText {
    isFocusable = true
    isFocusableInTouchMode = true
    val imm = CommonContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    requestFocus()
    imm.showSoftInput(this, 0)
    return this
}


// 隐藏键盘
fun EditText.hideKeyboard(): EditText {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
    return this
}

fun hideKeyboard() {
    val imm = CommonContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
}

/**
 * 光标移动到最后
 */
fun EditText.cursorMoveToEnd(): EditText {
    if (!text.isNullOrBlank())
        setSelection(text.length)
    return this
}

/**
 * 广播移动到指定位置
 */
fun EditText.cursorMoveToPosition(position: Int): EditText {
    if (!text.isNullOrBlank() && text.length >= position)
        setSelection(position)
    return this
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
    val locationManager =
        CommonContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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


/**
 * 获取屏幕宽高
 */
val screenWidth by lazy {
    screenWidth()
}
val screenHeight by lazy {
    getScreenRealHeight()
}

/**
 * 获取屏幕宽高
 */
fun screenWidth(): Int {
    return CommonContext.resources.displayMetrics.widthPixels
}


/**
 * 获取屏幕可用高度，不包含屏幕安全区
 */
fun screenHeight(): Int {
    return CommonContext.resources.displayMetrics.heightPixels
}

/**
 * 获取屏幕实际高度包含异形屏等安全区域
 */
fun getScreenRealHeight(): Int {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
        return screenHeight()
    }
    val windowManager = (CommonContext.getSystemService(Context.WINDOW_SERVICE)) as WindowManager
    val display = windowManager?.defaultDisplay
    val point = Point()
    display?.getRealSize(point)
    return point.y
}

/**
 * 获取view真实的宽高信息
 */
fun View.getRealInfo(): Array<Int> {
    val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    measure(widthSpec, heightSpec)
    val measuredWidth = measuredWidth
    val measuredHeight = measuredHeight
    return arrayOf(measuredWidth, measuredHeight)
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
