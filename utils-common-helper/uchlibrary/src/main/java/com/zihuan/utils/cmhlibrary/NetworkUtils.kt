package com.zihuan.utils.cmhlibrary

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build

/**
 * 网络状态监听工具类
 */

/**
 * 为了避免因多次接收到广播反复提醒的情况而设置的标志位，用于缓存收到新的广播前的网络状态
 */
private var tempState: State? = null

/**
 * 获取当前网络连接状态
 *
 * @return 网络状态
 */
fun Context.getConnectState(): State {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var networkInfo: NetworkInfo? = null
    if (manager != null) {
        networkInfo = manager.activeNetworkInfo
    }
    var state = State.UN_CONNECTED
    if (networkInfo != null && networkInfo.isAvailable) {
        if (isMobileConnected(this)) {
            state = State.MOBILE
        } else if (isWifiConnected(this)) {
            state = State.WIFI
        }
    }
    if (state == tempState) {
        return State.PUBLISHED
    }
    tempState = state
    return state
}

private fun isMobileConnected(context: Context): Boolean {
    return isConnected(context, ConnectivityManager.TYPE_MOBILE)
}

private fun isWifiConnected(context: Context): Boolean {
    return isConnected(context, ConnectivityManager.TYPE_WIFI)
}

fun isConnected(context: Context, type: Int): Boolean {
    //getAllNetworkInfo() 在 API 23 中被弃用
    //getAllNetworks() 在 API 21 中才添加
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        var allNetworkInfo = arrayOfNulls<NetworkInfo>(0)
        if (manager != null) {
            allNetworkInfo = manager.allNetworkInfo
        }
        for (info in allNetworkInfo) {
            if (info!!.type == type) {
                return info.isAvailable
            }
        }
    } else {
        var networks = arrayOfNulls<Network>(0)
        if (manager != null) {
            networks = manager.allNetworks
        }
        for (network in networks) {
            val networkInfo = manager.getNetworkInfo(network)
            if (networkInfo.type == type) {
                return networkInfo.isAvailable
            }
        }
    }
    return false
}

/**
 * 判断网络是否连接
 *
 * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
 *
 * @return `true`: 是<br></br>`false`: 否
 */
fun isConnected(context: Context): Boolean {
    val info = getActiveNetworkInfo(context)
    return info != null && info.isConnected
}

/**
 * 获取活动网络信息
 *
 * 需添加权限
 * `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
 *
 * @return NetworkInfo
 */
private fun getActiveNetworkInfo(context: Context): NetworkInfo {
    val manager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return manager.activeNetworkInfo
}

/**
 * 标记当前网络状态，分别是：移动数据、Wifi、未连接、网络状态已公布
 */
enum class State {
    MOBILE, WIFI, UN_CONNECTED, PUBLISHED
}