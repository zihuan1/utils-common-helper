package com.zihuan.utils.cmhlibrary

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.github.dfqin.grantor.PermissionListener
import com.github.dfqin.grantor.PermissionsUtil

/**
 *
 * @Description
 * @author zihuan
 * @date 2019/11/11 18:23
 */


inline fun Activity.requestMyPermission(vararg permissions: String, crossinline permissionListener: () -> Unit) {
    requestEasyPermission(*permissions, permissionListener = permissionListener)
}

inline fun Fragment.requestMyPermission(vararg permissions: String, crossinline permissionListener: () -> Unit) {
    requireContext().requestEasyPermission(*permissions, permissionListener = permissionListener)
}

inline fun View.requestMyPermission(vararg permissions: String, crossinline permissionListener: () -> Unit) {
    context.requestEasyPermission(*permissions, permissionListener = permissionListener)
}

inline fun Context.requestEasyPermission(vararg permissions: String, crossinline permissionListener: () -> Unit) {
    var permission = object : PermissionListener {
        override fun permissionGranted(permission: Array<out String>) {
//            toast("授权通过")
            permissionListener()
        }

        override fun permissionDenied(permission: Array<out String>) {
        }

    }
    PermissionsUtil.requestPermission(this, permission, * permissions)
}
