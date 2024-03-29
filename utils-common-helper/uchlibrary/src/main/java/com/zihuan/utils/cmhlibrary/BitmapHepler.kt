package com.zihuan.utils.cmhlibrary

import android.content.ContentValues
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.max
import kotlin.math.min


private val defPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
/**
 * 保存图片到SD卡
 *
 * @param fileName
 * @param quality     保存的质量（0-100）
 * @param fScale      压缩比率 1为原图
 *
 */
fun Bitmap.save(path: String = defPath, fileName: String, quality: Int = 100, fScale: Float = 1f): Boolean {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = CommonContext.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/PerracoLabs")
            }
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            resolver.openOutputStream(uri!!).use {
                val stream = BufferedOutputStream(it)
                val newBitmap = if (fScale == 1f) {
                    Bitmap.createBitmap(this)
                } else {
                    compressImage(this, fScale)
                }
                newBitmap.compress(Bitmap.CompressFormat.PNG, quality, stream)
                stream.flush() //输出
                stream.close() //关闭
                it!!.close()
                newBitmap.recycle()
            }
        } else {
            var url = ""
            val pFileDir = File(path)
            val pFilePath = File(pFileDir, fileName)
            if (!pFileDir.exists()) {
                pFileDir.mkdirs() //如果路径不存在就先创建路径
            }
            val it = FileOutputStream(pFilePath)
            val stream = BufferedOutputStream(it)
            val newBitmap = if (fScale == 1f) Bitmap.createBitmap(this) else compressImage(this, fScale)
            newBitmap.compress(Bitmap.CompressFormat.PNG, quality, stream)
            url = pFilePath.absolutePath
            stream.flush() //输出
            stream.close() //关闭
            it.close()
            newBitmap.recycle()
            //            检测图片是否被旋转
            val arg = readPictureDegree(url)
            if (arg != 0) {
                //修复旋转重新保存
                rotatingImageView(arg, this).save(path, fileName, quality, fScale)
            }
            //            MediaStore.Images.Media.insertImage(AppContext.contentResolver, url, fileName, null);
            MediaScannerConnection.scanFile(CommonContext, arrayOf(url), arrayOf("image/jpeg")) { path, uri ->
                CommonLogger("刷新完成 $path uri $uri")
            }
        }
    } catch (e: Exception) {
        CommonLogger("图片保存失败$e")
        return false
    }
    return true
}

/**
 * 压缩图片到原大小的scale倍 (100)
 * @param argBitmap 原图片
 * @return 压缩后的图片
 * */
fun compressImage(argBitmap: Bitmap, fScale: Float): Bitmap {
    /// 图片源
    var bm: Bitmap? = argBitmap
    /// 获得图片的宽高
    val width = bm!!.width
    val height = bm.height
    /// 设置想要的大小
    val newWidth = (width * fScale).toInt()
    val newHeight = (height * fScale).toInt()
    /// 计算缩放比例
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    /// 取得想要缩放的matrix参数
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)
    /// 得到新的图片
    val newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true)
    if (bm != newbm) {
        bm.recycle()
        bm = null
    }
    return newbm
}

/**
 *  drawable转Bitmap
 */
fun drawableToBitmap(vectorDrawableId: Int): Bitmap {
    var bitmap: Bitmap? = null
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
        val vectorDrawable: Drawable = CommonContext.getDrawable(vectorDrawableId)!!
        bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        vectorDrawable.draw(canvas)
    } else {
        bitmap = BitmapFactory.decodeResource(CommonContext.resources, vectorDrawableId)
    }
    return bitmap
}

/**
 * 读取图片属性：旋转的角度
 *
 * @param path 图片绝对路径
 * @return degree旋转的角度
 */
fun readPictureDegree(path: String): Int {
    var degree = 0
    try {
        val exifInterface = ExifInterface(path)
        when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
            ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
            ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return degree
}

/**
 * 旋转图片
 *
 * @param angle  被旋转角度
 * @param bitmap 图片对象
 * @return 旋转后的图片
 */
fun rotatingImageView(angle: Int, bitmap: Bitmap): Bitmap {
    var returnBm: Bitmap? = null
    // 根据旋转角度，生成旋转矩阵
    val matrix = Matrix()
    matrix.postRotate(angle.toFloat())
    try {
        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
        returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } catch (e: OutOfMemoryError) {
    }

    if (returnBm == null) {
        returnBm = bitmap
    }
    if (bitmap != returnBm) {
        bitmap.recycle()
    }
    return returnBm
}

/**
 * 剪切1:1图片
 */
fun cropImage(bitmap: Bitmap): Bitmap {
    val w = bitmap.width // 得到图片的宽，高
    val h = bitmap.height
    var retX = 0
    var retY = 0
    var newWidth = w
    var newHeight = w
    if (w >= h) {
        retX = (w - h) / 2
        retY = 0
        newWidth = h
        newHeight = h
    }
    return Bitmap.createBitmap(bitmap, retX, retY, newWidth, newHeight, null, false)
}


/**
 * 修改图片大小
 *
 * @param width  需要的宽
 * @param height 需要的高
 */
fun Bitmap.modifySize(width: Int, height: Int): Bitmap? {
    val matrix = Matrix()
    matrix.setScale(1f, 1f)
    return transform(matrix, this, width, height, 1 or 0x0)
}


/**
 * Transform source Bitmap to targeted width and height.
 */
private fun transform(scaler: Matrix?, source: Bitmap, targetWidth: Int, targetHeight: Int, options: Int): Bitmap {
    var scaler = scaler
    val scaleUp = options and 1 != 0
    val recycle = options and 1 != 0
    val deltaX = source.width - targetWidth
    val deltaY = source.height - targetHeight
    if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
        val b2 = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val c = Canvas(b2)

        val deltaXHalf = max(0, deltaX / 2)
        val deltaYHalf = max(0, deltaY / 2)
        val src = Rect(deltaXHalf, deltaYHalf, deltaXHalf + min(targetWidth, source.width), deltaYHalf + min(targetHeight, source.height))
        val dstX = (targetWidth - src.width()) / 2
        val dstY = (targetHeight - src.height()) / 2
        val dst = Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY)
        c.drawBitmap(source, src, dst, null)
        if (recycle) {
            source.recycle()
        }
        c.setBitmap(null)
        return b2
    }
    val bitmapWidthF = source.width.toFloat()
    val bitmapHeightF = source.height.toFloat()

    val bitmapAspect = bitmapWidthF / bitmapHeightF
    val viewAspect = targetWidth.toFloat() / targetHeight

    if (bitmapAspect > viewAspect) {
        val scale = targetHeight / bitmapHeightF
        if (scale < .9f || scale > 1f) {
            scaler!!.setScale(scale, scale)
        } else {
            scaler = null
        }
    } else {
        val scale = targetWidth / bitmapWidthF
        if (scale < .9f || scale > 1f) {
            scaler!!.setScale(scale, scale)
        } else {
            scaler = null
        }
    }

    val b1: Bitmap
    b1 = if (scaler != null) Bitmap.createBitmap(source, 0, 0, source.width, source.height, scaler, true) else {
        source
    }

    if (recycle && b1 != source) {
        source.recycle()
    }

    val dx1 = max(0, b1.width - targetWidth)
    val dy1 = max(0, b1.height - targetHeight)

    val b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight)

    if (b2 != b1) {
        if (recycle || b1 != source) {
            b1.recycle()
        }
    }
    return b2
}

/**
 * 对图片进行 高斯模糊
 *
 * @param bmp
 * @return
 */
fun boxBlurFilter(bmp: Bitmap): Bitmap {
    /** 水平方向模糊度  */
    val hRadius = 10f

    /** 竖直方向模糊度  */
    val vRadius = 10f

    /** 模糊迭代度  */
    val iterations = 7
    val width = bmp.width
    val height = bmp.height
    val inPixels = IntArray(width * height)
    val outPixels = IntArray(width * height)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bmp.getPixels(inPixels, 0, width, 0, 0, width, height)
    for (i in 0 until iterations) {
        blur(inPixels, outPixels, width, height, hRadius)
        blur(outPixels, inPixels, height, width, vRadius)
    }
    bitmap.setPixels(inPixels, 0, width, 0, 0, width, height)
    return bitmap
}

fun blur(`in`: IntArray, out: IntArray, width: Int, height: Int, radius: Float) {
    val widthMinus1 = width - 1
    val r = radius.toInt()
    val tableSize = 2 * r + 1
    val divide = IntArray(256 * tableSize)
    for (i in 0 until 256 * tableSize) {
        divide[i] = i / tableSize
    }
    var inIndex = 0
    for (y in 0 until height) {
        var outIndex = y
        var ta = 0
        var tr = 0
        var tg = 0
        var tb = 0
        for (i in -r..r) {
            val rgb = `in`[inIndex + clamp(i, 0, width - 1)]
            ta += rgb shr 24 and 0xff
            tr += rgb shr 16 and 0xff
            tg += rgb shr 8 and 0xff
            tb += rgb and 0xff
        }
        for (x in 0 until width) {
            out[outIndex] = (divide[ta] shl 24 or (divide[tr] shl 16) or (divide[tg] shl 8) or divide[tb])
            var i1 = x + r + 1
            if (i1 > widthMinus1) i1 = widthMinus1
            var i2 = x - r
            if (i2 < 0) i2 = 0
            val rgb1 = `in`[inIndex + i1]
            val rgb2 = `in`[inIndex + i2]
            ta += (rgb1 shr 24 and 0xff) - (rgb2 shr 24 and 0xff)
            tr += (rgb1 and 0xff0000) - (rgb2 and 0xff0000) shr 16
            tg += (rgb1 and 0xff00) - (rgb2 and 0xff00) shr 8
            tb += (rgb1 and 0xff) - (rgb2 and 0xff)
            outIndex += height
        }
        inIndex += width
    }
}


fun clamp(x: Int, a: Int, b: Int): Int {
    return if (x < a) a else if (x > b) b else x
}

/**
 * 截图
 * @param height 指定高度
 */
fun View.toBitmap(setHeight: Int = 0): Bitmap {
    val screenshot = Bitmap.createBitmap(width, if (setHeight == 0) height else setHeight, Bitmap.Config.ARGB_8888)
    val c = Canvas(screenshot)
    draw(c)
    return screenshot
}

/**
 * View转Bitmap
 */
//fun View.toBitmap(): Bitmap {
//    // 创建对应大小的bitmap
//    var viewHeight = 0
//    if (this is ViewGroup) {
//        for (i in 0 until childCount) {
//            viewHeight += getChildAt(i).height
//        }
//    } else {
//        height
//    }
//    var bitmap = Bitmap.createBitmap(width, viewHeight, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(bitmap)
//    draw(canvas)
//    return bitmap
//}


fun View.toBitmap2(): Bitmap {
    val screenshot: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    val c = Canvas(screenshot)
    c.translate(-scrollX.toFloat(), -scrollY.toFloat())
    draw(c)
    return screenshot
}

/**
 * View保存成PNG图片
 */
fun View.toPng(path: String, name: String) {
    toBitmap().save(path, name, 100, 1f)
}

