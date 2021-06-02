package com.zihuan.utils.cmhlibrary

import android.Manifest
import com.github.dfqin.grantor.PermissionListener
import com.github.dfqin.grantor.PermissionsUtil

/**
 *
 * @Description
 * @author zihuan
 * @date 2019/11/11 18:23
 */

/**
 * 请求存储权限
 */
inline fun storagePermission(crossinline action: (pass: Boolean) -> Unit) {
    requestEasyPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,action=action)
}

/**
 * 相机权限
 */
inline fun cameraPermission(crossinline action: (pass: Boolean) -> Unit) {
    requestEasyPermission(Manifest.permission.CAMERA,action=action)
}

inline fun requestEasyPermission(vararg permissions: String, crossinline action: (pass: Boolean) -> Unit) {
    var permission = object : PermissionListener {
        override fun permissionGranted(permission: Array<out String>) {
            action(true)
        }

        override fun permissionDenied(permission: Array<out String>) {
            action(false)
        }

    }
    PermissionsUtil.requestPermission(CommonContext, permission, * permissions)
}
