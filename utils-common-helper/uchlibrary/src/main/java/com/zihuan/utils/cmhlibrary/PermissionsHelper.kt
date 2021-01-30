package com.zihuan.utils.cmhlibrary

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


inline fun Fragment.requestEasyPermission(vararg permissions: String, crossinline permissionListener: (pass: Boolean) -> Unit) {
    requireContext().requestEasyPermission(*permissions, permissionListener = permissionListener)
}

inline fun View.requestEasyPermission(vararg permissions: String, crossinline permissionListener: (pass: Boolean) -> Unit) {
    context.requestEasyPermission(*permissions, permissionListener = permissionListener)
}

inline fun Context.requestEasyPermission(vararg permissions: String, crossinline permissionListener: (pass: Boolean) -> Unit) {
    var permission = object : PermissionListener {
        override fun permissionGranted(permission: Array<out String>) {
//            toast("授权通过")
            permissionListener(true)
        }

        override fun permissionDenied(permission: Array<out String>) {
            permissionListener(false)
        }

    }
    PermissionsUtil.requestPermission(this, permission, * permissions)
}
