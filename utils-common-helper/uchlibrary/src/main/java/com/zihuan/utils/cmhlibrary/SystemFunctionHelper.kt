package com.zihuan.utils.cmhlibrary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import android.provider.Settings
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import android.text.format.Formatter

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
fun shareSystem(filePath: String, type: String = "text") {
    val file = File(filePath)
    var shareIntent = Intent()
    //切记需要使用Intent.createChooser，否则会出现别样的应用选择框
    shareIntent = Intent.createChooser(shareIntent, file.name)
    // 判断版本大于等于7.0
    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        // "项目包名.fileprovider"即是在清单文件中配置的authorities
        // 给目标应用一个临时授权
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        FileProvider.getUriForFile(CommonContext, "${CommonContext.packageName}.provider", file)
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
    shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;

    CommonContext.startActivity(shareIntent)
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
fun refreshFile(filePath: String, action: () -> Unit) {
    val file = File(filePath)
    val mtm = MimeTypeMap.getSingleton()
    MediaScannerConnection.scanFile(
        CommonContext,
        arrayOf(file.toString()),
        arrayOf(
            mtm.getMimeTypeFromExtension(
                file.toString().substring(file.toString().lastIndexOf(".") + 1)
            )
        )
    ) { path, uri -> action() }
}

/**
 * 通知系统刷新媒体库
 * @param filePath 路径
 */
fun refreshGallery(filePath: String) {
    val localUri = Uri.fromFile(File(filePath))
    val localIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri)
    CommonContext.sendBroadcast(localIntent)
}


/**
 * 改变当前app亮度
 *
 */
fun Activity.changeBrightness(brightness: Float) {
    val window = window
    val lp = window.attributes
    lp.screenBrightness = brightness
    window.attributes = lp
}

/**
 * 获取当前系统亮度
 */
fun getBrightness(): Int {
    var systemBrightness = 0
    try {
        systemBrightness =
            Settings.System.getInt(CommonContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
    } catch (e: Settings.SettingNotFoundException) {
    }
    return systemBrightness
}

private var mAudioManager: AudioManager? = null
    get() {
        if (field == null) {
            initAudioManager()
        }
        return field
    }

/**
 * 初始化音频管理器
 */
private fun initAudioManager() {
    mAudioManager = CommonContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    mAudioManager?.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            mAudioManager?.requestAudioFocus(AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).build())
//        }
}

/**
 * 获取音量值
 *
 * @return 音量值
 */
fun getVolume() = mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0

/**
 * 获取最大音量
 *
 * @return 音量值
 */
fun getMaxVolume() = mAudioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 0

/**
 * 设置音量
 *
 * @param volume 音量值
 */
fun setVolume(volume: Int) {
    mAudioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
}

/**
 * 释放音频焦点
 */
fun releaseAudioManager() {
    //放弃音频焦点。使以前的焦点所有者(如果有的话)接收焦点。
    mAudioManager?.abandonAudioFocus(null)
//    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//        mAudioManager?.abandonAudioFocusRequest(
//            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).build()
//        )
//    }
    //置空
    mAudioManager = null
}

/**
 * 获取已用存储空间
 *
 * @return 以M,G为单位的容量
 */
fun getMemorySize(): String {
    val file = Environment.getDataDirectory()
    val statFs = StatFs(file.path)
    val blockSizeLong = statFs.blockSizeLong
    val blockCountLong = statFs.blockCountLong
    val size = blockCountLong * blockSizeLong
    return Formatter.formatFileSize(CommonContext, size)
}


/**
 * 获取手机可用存储空间
 *
 * @return 以M,G为单位的容量
 */
fun getAvailableMemorySize(): String {
    val file = Environment.getDataDirectory()
    val statFs = StatFs(file.path)
    val availableBlocksLong = statFs.availableBlocksLong
    val blockSizeLong = statFs.blockSizeLong
    return Formatter.formatFileSize(CommonContext, availableBlocksLong * blockSizeLong)
}


/**
 * 获取当前电池是否充电
 */
fun isCharging(): Boolean {
    val batteryBroadcast =
        CommonContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    return batteryBroadcast.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) != 0
}

/**
 * @return 2（BatteryManager.BATTERY_STATUS_CHARGING） 充电中 5（BatteryManager.BATTERY_STATUS_FULL）充电完成
 */
fun batterState(): Int {
    val batteryBroadcast =
        CommonContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    return batteryBroadcast.getIntExtra(
        BatteryManager.EXTRA_STATUS,
        BatteryManager.BATTERY_STATUS_UNKNOWN
    )
}

/**
 * 获取手机当前电量
 */
fun getCurrentBattery(): Int {
    val batteryManager = CommonContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    } else {
        val intent =
            CommonContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        intent.getIntExtra(
            BatteryManager.EXTRA_LEVEL,
            -1
        ) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
    }
}