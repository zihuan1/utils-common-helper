package com.zihuan.utils.cmhlibrary

import android.Manifest
import android.os.Build
import com.github.dfqin.grantor.PermissionListener
import com.github.dfqin.grantor.PermissionsUtil

/**
 * 请求存储权限
 */
inline fun storagePermission(crossinline action: (pass: Boolean) -> Unit) {
    val permission = if (Build.VERSION.SDK_INT >= 33) {
        arrayOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    requestEasyPermission(*permission) {
        action(it)
    }
}

inline fun storage(crossinline action: () -> Unit) {
    storagePermission { if (it) action() }
}

/**
 * 相机权限
 */
inline fun cameraPermission(crossinline action: (pass: Boolean) -> Unit) {
    requestEasyPermission(Manifest.permission.CAMERA, action = action)
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
