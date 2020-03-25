package com.zihuan.utils.cmhlibrary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.util.*

val SELECT_FILE_REQUESTCODE = 10086

/**
 * 打开系统拨号页面
 */
fun openSystemCall(phone: String) {
    val intent = Intent()
    intent.action = Intent.ACTION_CALL
    intent.data = Uri.parse("tel:$phone")
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    CommonContext.startActivity(intent)
}

/**
 * 打开系统相机
 */
//fun openSystemCamera(activity: Activity, requestCode: Int) {
//    var mImgName = "${System.currentTimeMillis()}.jpg"
//    val take = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//    val file = File(FileUtils.createNewFile() + File.separator, mImgName)
//    //处理android N以上
//    var mUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//        FileProvider.getUriForFile(activity, CommonContext.packageName + ".fileProvider", file)
//    } else {
//        Uri.fromFile(file)
//    }
//    take.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
//    activity.startActivityForResult(take, requestCode)//采用ForResult打开
//}

/**
 * 获取相册第一张缩略图
 */
fun getBitmapFromAlbum(): Bitmap? {
    val albumList = getPhotoAlbum()
    return if (albumList != null && albumList.isNotEmpty()) {
        MediaStore.Images.Thumbnails.getThumbnail(
            CommonContext.contentResolver,
            albumList[0].toLong(),
            MediaStore.Images.Thumbnails.MICRO_KIND,
            null
        )
    } else null
}

/**
 * 方法描述：按相册获取图片信息
 */
// 设置获取图片的字段信息
private val STORE_IMAGES = arrayOf(
    MediaStore.Images.Media.DISPLAY_NAME, // 显示的名称
    MediaStore.Images.Media.DATA, MediaStore.Images.Media.LONGITUDE, // 经度
    MediaStore.Images.Media._ID, // id
    MediaStore.Images.Media.BUCKET_ID, // dir id 目录
    MediaStore.Images.Media.BUCKET_DISPLAY_NAME // dir name 目录名字
)

/**
 * 获取相册集合
 *
 * @return
 */
fun getPhotoAlbum(): List<Int> {
    val aibumList = ArrayList<Int>()
    val cursor = MediaStore.Images.Media.query(
        CommonContext.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES
    )
    while (cursor.moveToNext()) {
        val id = cursor.getString(3)
        aibumList.add(Integer.parseInt(id))
    }
    cursor.close()
    return aibumList
}

/**
 * 分享文件
 * 分享文件类型
 */
fun Context.shareSystem(filePath: String, type: String = "text") {
    val file = File(filePath)
    var shareIntent = Intent()
    // 判断版本大于等于7.0
    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        // "项目包名.fileprovider"即是在清单文件中配置的authorities
        // 给目标应用一个临时授权
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        FileProvider.getUriForFile(this, "$packageName.provider", file)
    } else {
        Uri.fromFile(file)
    }
    shareIntent.action = Intent.ACTION_SEND
    var fileType = when {
        file.name.endsWith("mp4") -> {
            "video"
        }
        file.name.endsWith("png") || file.name.endsWith("jpg") -> {
            "image"
        }
        file.name.endsWith("text") -> {
            "text"
        }
        else -> {
            type
        }
    }
    shareIntent.type = "$fileType/*"
    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
//切记需要使用Intent.createChooser，否则会出现别样的应用选择框
    shareIntent = Intent.createChooser(shareIntent, file.name)
    startActivity(shareIntent)
}

/**
 * 打开系统管理器,选择文件并返回文件路径
 */
fun Activity.selectFile() {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "*/*"
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    startActivityForResult(intent, SELECT_FILE_REQUESTCODE)
}

/**
 * 通知系统刷新指定路径
 * @param filePath 刷新路径
 * @param action 刷新完成回调
 * 这个方法如果不好使,就用下面的方法
 * <code>{@link #refreshGallery}</code>.
 */
fun Context.refreshFile(filePath: String, action: () -> Unit) {
    val file = File(filePath)
    val mtm = MimeTypeMap.getSingleton()
    MediaScannerConnection.scanFile(
        this,
        arrayOf(file.toString()),
        arrayOf(mtm.getMimeTypeFromExtension(file.toString().substring(file.toString().lastIndexOf(".") + 1)))
    ) { path, uri -> action() }
}

/**
 * 通知系统刷新媒体库
 * @param filePath 路径
 */
fun Context.refreshGallery(filePath: String) {
    val localUri = Uri.fromFile(File(filePath))
    val localIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri)
    sendBroadcast(localIntent)
}

