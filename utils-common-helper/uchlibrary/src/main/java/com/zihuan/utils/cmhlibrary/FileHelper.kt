package com.zihuan.utils.cmhlibrary

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat.getExternalFilesDirs
import org.jetbrains.annotations.NotNull
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


/***
 * 文件工具类
 */
const val SIZETYPE_B = 1 //获取文件大小单位为B的double值

const val SIZETYPE_KB = 2 //获取文件大小单位为KB的double值

const val SIZETYPE_MB = 3 //获取文件大小单位为MB的double值

const val SIZETYPE_GB = 4 //获取文件大小单位为GB的double值

/**获取文件的md5**/
fun getFileMD5(@NotNull file: File): String? {
    var digest: MessageDigest = MessageDigest.getInstance("MD5")
    var fin = FileInputStream(file)
    val buffer = ByteArray(1024)
    var len: Int = -1
    fin.use {
        while ((fin.read(buffer, 0, 1024).also { len = it }) != -1) {
            digest.update(buffer, 0, len)
        }
    }
    val bigInt = BigInteger(1, digest!!.digest())
    return bigInt.toString(16)
}

/** 读取文件夹下的所有的文件路径**/
fun getFilesPath(@NotNull path: String, @NotNull files: ArrayList<File>): List<File> {
    var realFile = File(path)
    if (realFile.isDirectory) {
        var subFiles = realFile.listFiles()
        subFiles.forEach {
            if (it.isDirectory) {
                getFilesPath(it.absolutePath, files)
            } else {
                files.add(it)
            }
            CommonLogger("文件夹" + it.absoluteFile)
        }
    }
    return files
}

/**
 *  将多个文件夹的内容合并到一个新文件
 * 与 getFilesPath()方法结合使用
 *      Demo
 *      var list = ArrayList<File>()
 *      FileUtils.getFilesPath(Environment.getExternalStorageDirectory().toString() + "/src/", list)
 *      FileUtils.stringMerge(list)
 * **/
fun stringMerge(path: String): String {
    var outPath =
        Environment.getExternalStorageDirectory().toString() + "/amergecode"
    val file = File(outPath)
    if (!file.exists()) {
        file.mkdir()
        CommonLogger("创建文件夹")
    }
    val name = path.split("/").let {
        it[it.lastIndex]
    }
    val file2 = File(file.absolutePath + "/$name${System.currentTimeMillis()}.txt")
    if (!file2.exists()) {
        file2.createNewFile()
        CommonLogger("创建文件")
    }
    var outOs = FileOutputStream(file2)
    outOs.use {
        it.channel.use { fileChannel ->
            getFilesPath(path, ArrayList()).forEach { it ->
                FileInputStream(it.absoluteFile).channel.use { it ->
                    it.transferTo(
                        0,
                        it.size(),
                        fileChannel
                    )
                }
            }
        }
    }
    return file2.absolutePath
}

val FILENAME = "tripsTemp"

/**
 * 创建文件夹
 * @param fileName 文件夹名字
 * @return 文件夹路径
 */
fun createNewFile(fileName: String = FILENAME): String {
    val file =
        File(Environment.getExternalStorageDirectory().toString() + File.separator + fileName)
    if (!file.exists()) {
        file.mkdirs()
    }
    return file.toString()
}


/**
 * 获取图片路径
 * @return 图片路径
 */
fun getImgPath(name: String): String {
    return createNewFile(FILENAME) + File.separator + name
}


/**
 * 得到资源文件中图片的Uri
 *
 * @param context 上下文对象
 * @param id      资源id
 * @return Uri
 */
fun getUriFromDrawableRes(context: Context, id: Int): Uri {
    val resources = context.resources
    val path = (ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
            + resources.getResourcePackageName(id) + "/"
            + resources.getResourceTypeName(id) + "/"
            + resources.getResourceEntryName(id))
    return Uri.parse(path)
}

//读取指定格式的文件
fun getSpecificTypeOfFile(context: Context, extension: Array<String>): ArrayList<String> {
    var uriList = ArrayList<String>()
    //从外存中获取
    val fileUri = MediaStore.Files.getContentUri("external")
    //筛选列，这里只筛选了：文件路径和不含后缀的文件名
    val projection =
        arrayOf(MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE)
    //构造筛选语句
    var selection = ""
    for (i in extension.indices) {
        if (i != 0) {
            selection = "$selection OR "
        }
        selection =
            selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'"
    }
    //按时间递增顺序对结果进行排序;待会从后往前移动游标就可实现时间递减
    val sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED
    //获取内容解析器对象
    val resolver = context.contentResolver
    //获取游标
    val cursor = resolver.query(fileUri, projection, selection, null, sortOrder)
        ?: return uriList
    //游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
    if (cursor.moveToLast()) {
        do {
            //输出文件的完整路径
            val data = cursor.getString(0)
            Log.e("本地文件tag", data)
            uriList.add(data)
        } while (cursor.moveToPrevious())
    }
    cursor.close()
    return uriList
}

// 判断SD卡是否存在 判断是是内置的sd卡而不是外置的可移动sd卡
fun isExistSDCard(): Boolean {
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}

/**
 * 删除文件，可以是文件或文件夹
 *
 * @return 删除成功返回true，否则返回false
 */
fun File.deleteFile(): Boolean {
    return if (!exists()) {
        CommonLogger("删除文件失败:" + absolutePath + "不存在！")
        false
    } else {
        if (isFile)
            deleteSingleFile(absolutePath)
        else
            deleteDirectory(absolutePath)
    }
}

/**
 * 删除单个文件
 *
 * @param filePathName 要删除的文件的文件名
 * @return 单个文件删除成功返回true，否则返回false
 */
fun deleteSingleFile(filePathName: String): Boolean {
    val file = File(filePathName)
    // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
    return if (file.exists() && file.isFile) {
        if (file.delete()) {
            CommonLogger("Copy_Delete.deleteSingleFile: 删除单个文件" + filePathName + "成功！")
            true
        } else {
            CommonLogger("删除单个文件" + filePathName + "失败！")
            false
        }
    } else {
        CommonLogger("删除单个文件失败：" + filePathName + "不存在！")
        false
    }
}

/**
 * 删除目录及目录下的文件
 *
 * @param filePath 要删除的目录的文件路径
 * @return 目录删除成功返回true，否则返回false
 */
fun deleteDirectory(filePath: String): Boolean {
    var filePath = filePath
    // 如果dir不以文件分隔符结尾，自动添加文件分隔符
    if (!filePath.endsWith(File.separator))
        filePath += File.separator
    val dirFile = File(filePath)
    // 如果dir对应的文件不存在，或者不是一个目录，则退出
    if (!dirFile.exists() || !dirFile.isDirectory) {
        CommonLogger("删除目录失败：" + filePath + "不存在！")
        return false
    }
    var flag = true
    // 删除文件夹中的所有文件包括子目录
    val files = dirFile.listFiles()
    for (file in files!!) {
        // 删除子文件
        if (file.isFile) {
            flag = deleteSingleFile(file.absolutePath)
            if (!flag)
                break
        } else if (file.isDirectory) {
            flag = deleteDirectory(
                file
                    .absolutePath
            )
            if (!flag)
                break
        }// 删除子目录
    }
    if (!flag) {
        CommonLogger("删除目录失败！")
        return false
    }
    // 删除当前目录
    return if (dirFile.delete()) {
        CommonLogger("Copy_Delete.deleteDirectory: 删除目录" + filePath + "成功！")
        true
    } else {
        CommonLogger("删除目录：" + filePath + "失败！")
        false
    }
}

/**
 * 从Uri获取文件绝对路径
 *专为Android4.4以上设计的从Uri获取文件路径
 */
fun Context.getRealFilePath(uri: Uri): String? {
    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        } else if (isDownloadsDocument(uri)) {

            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
            )

            return getDataColumn(this, contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])

            return getDataColumn(this, contentUri, selection, selectionArgs)
        }// MediaProvider
        // DownloadsProvider
    } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
        return getDataColumn(this, uri, null, null)
    } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
        return uri.path
    }// File
    // MediaStore (and general)
    return null
}

/**
 * Get the value of the data column for this Uri. This is useful for
 * MediaStore Uris, and other file-based ContentProviders.
 * @param context       The context.
 * @param uri           The Uri to query.
 * @param selection     (Optional) Filter used in the query.
 * @param selectionArgs (Optional) Selection arguments used in the query.
 * @return The value of the _data column, which is typically a file path.
 */
fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    try {
        cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(columnIndex)
        }
    } catch (ignored: Exception) {
    } finally {
        cursor?.close()
    }
    return null
}

/**
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
private fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

/**
 * @return Whether the Uri authority is DownloadsProvider.
 */
private fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

/**
 * @return Whether the Uri authority is MediaProvider.
 */
private fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

/**
 * 获取文件指定文件的指定单位的大小
 *
 * @param filePath 文件路径
 * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
 * @return double值的大小
 */
fun getFileOrFilesSize(filePath: String, sizeType: Int): Double {
    val file = File(filePath)
    var blockSize: Long = 0
    try {
        blockSize = if (file.isDirectory) {
            getFileSizes(file)
        } else {
            getFileSize(file)
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return formatFileSize(blockSize, sizeType)
}

/**
 * 调用此方法自动计算指定文件或指定文件夹的大小
 *
 * @param filePath 文件路径
 * @return 计算好的带B、KB、MB、GB的字符串
 */
fun getAutoFileOrFilesSize(filePath: String): String {
    val file = File(filePath)
    var blockSize: Long = 0
    try {
        blockSize = if (file.isDirectory) {
            getFileSizes(file)
        } else {
            getFileSize(file)
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return formatFileSize(blockSize)
}

/**
 * 获取指定文件大小
 *
 * @param file
 * @return
 * @throws Exception
 */
fun getFileSize(file: File): Long {
    var size: Long = 0
    try {
        if (file.exists()) {
            var fis: FileInputStream? = null
            fis = FileInputStream(file)
            size = fis.available().toLong()
        } else {
            file.createNewFile()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return size
}

/**
 * 获取指定文件夹
 *
 * @param f
 * @return
 */
private fun getFileSizes(f: File): Long {
    var size: Long = 0
    val flist = f.listFiles()
    for (i in flist.indices) {
        size = if (flist[i].isDirectory) {
            size + getFileSizes(flist[i])
        } else {
            size + getFileSize(flist[i])
        }
    }
    return size
}

/**
 * 转换文件大小
 *
 * @param fileS
 * @return
 */
fun formatFileSize(fileS: Long): String {
    val df = DecimalFormat("#.00")
    var fileSizeString = ""
    val wrongSize = "0B"
    if (fileS == 0L) {
        return wrongSize
    }
    fileSizeString = if (fileS < 1024) {
        df.format(fileS.toDouble()) + "B"
    } else if (fileS < 1048576) {
        df.format(fileS.toDouble() / 1024) + "KB"
    } else if (fileS < 1073741824) {
        df.format(fileS.toDouble() / 1048576) + "MB"
    } else {
        df.format(fileS.toDouble() / 1073741824) + "GB"
    }
    return fileSizeString
}

/**
 * 转换文件大小,指定转换的类型
 *
 * @param fileS
 * @param sizeType
 * @return
 */
private fun formatFileSize(fileS: Long, sizeType: Int): Double {
    val df = DecimalFormat("#.00")
    var fileSizeLong = 0.0
    when (sizeType) {
        SIZETYPE_B -> fileSizeLong = java.lang.Double.valueOf(df.format(fileS.toDouble()))
        SIZETYPE_KB -> fileSizeLong =
            java.lang.Double.valueOf(df.format(fileS.toDouble() / 1024))
        SIZETYPE_MB -> fileSizeLong =
            java.lang.Double.valueOf(df.format(fileS.toDouble() / 1048576))
        SIZETYPE_GB -> fileSizeLong =
            java.lang.Double.valueOf(df.format(fileS.toDouble() / 1073741824))
        else -> {
        }
    }
    return fileSizeLong
}

fun reNameFile(file: File, fileName: String): String? {
    val FILE_DOWNLOAD_DIR = ""
    if (File(fileName).exists()) {
        val baseFile = File(FILE_DOWNLOAD_DIR)
        val fileFilter = FileFilter { pathname -> pathname.name.startsWith(fileName) }
        val files = baseFile.listFiles(fileFilter)
        val fileList = Arrays.asList(*files)
        fileList.sortWith(Comparator { o1, o2 -> o1.name.compareTo(o2.name) })
        val lastFile = fileList[0]
        val indexStr = lastFile.name.split(fileName).toTypedArray()
        var index = 0
        if (indexStr.size > 1) {
            indexStr[1].split("\\(").toTypedArray()[1].split("\\)").toTypedArray()[0].toInt()
            index++
        }
        val newName = "$fileName($index)"
        val dest = File(FILE_DOWNLOAD_DIR + newName)
        file.renameTo(dest)
        return newName
    } else {
        val dest = File(fileName)
        file.renameTo(dest)
    }
    return fileName
}

/**
 * 获取次存储卡路径,一般就是外置 TF 卡了. 不过也有可能是 USB OTG 设备...
 * 其实只要判断第二章卡在挂载状态,就可以用了
 * 获取的是一个外置存储列表
 */
fun Context.getSecondaryStoragePath(): String {
    var storage = getExternalFilesDirs(this, Environment.MEDIA_MOUNTED)
    storage.forEach {
        CommonLogger(it.name + " " + it.path + " " + it.absolutePath + " " + it.absoluteFile)
    }
    return ""
}


/**
 * 通过反射调用获取内置存储和外置sd卡根路径(通用)
 *
 * @param mContext    上下文
 * @param is_removale 是否可移除，false返回内部存储路径，true返回外置SD卡路径
 * @return
 */
fun Context.getStoragePath(is_removale: Boolean = true): String? {
    var path = ""
    //使用getSystemService(String)检索一个StorageManager用于访问系统存储功能。
    val mStorageManager = getSystemService(Context.STORAGE_SERVICE) as StorageManager
    var storageVolumeClazz: Class<*>?
    try {
        storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
        val getVolumeList = mStorageManager.javaClass.getMethod("getVolumeList")
        val getPath = storageVolumeClazz.getMethod("getPath")
        val isRemovable = storageVolumeClazz.getMethod("isRemovable")
        val result = getVolumeList.invoke(mStorageManager) as Array<Any>

        for (i in result.indices) {
            val storageVolumeElement = result[i]
            path = getPath.invoke(storageVolumeElement) as String
            val removable = isRemovable.invoke(storageVolumeElement) as Boolean
            //返回外部存储路径
            if (is_removale != removable) {
//                return if (checkMounted(path)) path else null
                continue
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return if (checkMounted(path)) path else null
}

/**
 * 是否挂载
 */
fun Context.checkMounted(mountPoint: String?): Boolean {
    if (mountPoint.isNullOrBlank()) {
        return false
    }
    val storageManager = getSystemService(Context.STORAGE_SERVICE) as StorageManager
    try {
        val getVolumeState = storageManager.javaClass.getMethod(
            "getVolumeState", String::class.java
        )
        val state = getVolumeState.invoke(storageManager, mountPoint) as String
        return Environment.MEDIA_MOUNTED == state
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

/**
 * 重命名文件
 * @param path 文件路径
 * @param newName 新的名字
 */
fun renameFile(path: String, newName: String) {
    val file = File(path)
    val oldPath = file.absolutePath
    val lastDot = path.lastIndexOf(".")
    var name = newName + path.substring(lastDot)
    if (!TextUtils.isEmpty(oldPath)) {
        var newPath = oldPath.replace(file.name, name)
        renameFile2(oldPath, newPath)
    }
}
/**
 * 重命名文件并且覆盖后缀名
 * @param path 文件路径
 * @param newName 新的名字
 */
fun renameFileFill(path: String, newName: String) {
    val file = File(path)
    val oldPath = file.absolutePath
    if (!TextUtils.isEmpty(oldPath)) {
        var newPath = oldPath.replace(file.name, newName)
        renameFile2(oldPath, newPath)
    }
}

/**
 * oldPath 和 newPath必须是新旧文件的绝对路径
 */
private fun renameFile2(oldPath: String, newPath: String) {
    if (TextUtils.isEmpty(oldPath)) {
        return
    }

    if (TextUtils.isEmpty(newPath)) {
        return
    }

    val file = File(oldPath)
    file.renameTo(File(newPath))
}

/** 将多个文件夹的内容合并到一个新文件
 * 一般用于申请软著之类的需要3000行代码
 */
fun stringMerge(files: List<File>) {
    var outPath =
        Environment.getExternalStorageDirectory().toString() + "/amergecode/mergecode1.txt"
    var outOs = FileOutputStream(File(outPath))
    outOs.use { stream ->
        stream.channel.use { fileChannel ->
            files.forEach { file ->
                FileInputStream(file.absoluteFile).channel.use {
                    it.transferTo(
                        0,
                        it.size(),
                        fileChannel
                    )
                }
            }
        }
    }
}
