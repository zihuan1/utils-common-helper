package com.zihuan.utils.cmhlibrary

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.util.*


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
        MediaStore.Images.Thumbnails.getThumbnail(CommonContext.contentResolver, albumList[0].toLong(), MediaStore.Images.Thumbnails.MICRO_KIND, null)
    } else null
}

/**
 * 方法描述：按相册获取图片信息
 */
// 设置获取图片的字段信息
private val STORE_IMAGES = arrayOf(MediaStore.Images.Media.DISPLAY_NAME, // 显示的名称
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
    val cursor = MediaStore.Images.Media.query(CommonContext.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES)
    while (cursor.moveToNext()) {
        val id = cursor.getString(3)
        aibumList.add(Integer.parseInt(id))
    }
    cursor.close()
    return aibumList
}
