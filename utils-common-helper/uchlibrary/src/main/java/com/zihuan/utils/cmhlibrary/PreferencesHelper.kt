package com.zihuan.utils.cmhlibrary

import android.content.Context
import android.util.Base64
import android.widget.Toast
import org.jetbrains.annotations.Nullable
import java.io.*


/**
 * 如果不想用代理的方式存取值,可以用一些方法
 * 存储工作是异步的
 */

private var mName = "commonDefaultName"
fun setPreferencesName(@Nullable name: String) {
    mName = name
}

private val prefs by lazy {
    CommonContext.getSharedPreferences(mName, Context.MODE_PRIVATE)
}


/**
 * 存储值
 * Double类型的数据会自动转换成String类型存储
 */
fun Number.savePreference(key: String) {
    putPreference(key, this)
}

fun String.savePreference(key: String) {
    putPreference(key, this)
}

fun Boolean.savePreference(key: String) {
    putPreference(key, this)
}


/**
 * 快速的获取值
 * var value=getCommonPreference()
 */
fun <T : Any> findPreference(key: String, defValue: T): T {
    return getPreference(key, defValue)
}

internal fun <T> putPreference(key: String, value: T) {
    prefs.edit().apply {
        when (value) {
            is String, is Double -> putString(key, value.toString())
            is Int -> putInt(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
            is Boolean -> putBoolean(key, value)
            else -> Toast.makeText(CommonContext, "Preference不支持的类型$value", Toast.LENGTH_LONG)
                .show()
        }
        if (preferenceAsyn) apply() else commit()
    }
}

internal fun <T> getPreference(key: String, defValue: T): T {
    return with(prefs) {
        when (defValue) {
            is String -> getString(key, defValue)
            is Int -> getInt(key, defValue)
            is Float -> getFloat(key, defValue)
            is Long -> getLong(key, defValue)
            is Boolean -> getBoolean(key, defValue)
            else -> Toast.makeText(CommonContext, "Preference不支持的类型$defValue", Toast.LENGTH_LONG)
                .show()
        }
    } as T
}

/**
 * 清除所有数据
 */
fun clearPreference() {
    prefs.edit().clear().commit()
}
//    存储集合
fun putHashMap(key: String, hashmap: HashMap<String, Int>): Boolean {
    val settings = PreferenceManager.getDefaultSharedPreferences(MainApplication.getInstance())
    val editor = settings.edit()
    try {
        val liststr = SceneList2String(hashmap)
        editor.putString(key, liststr)
    } catch (e: IOException) {
        showToast("存储Map错误$e")
    }

    return editor.commit()
}
//
fun getHashMap(key: String): HashMap<String, Int>? {
    val settings = PreferenceManager.getDefaultSharedPreferences(MainApplication.getInstance())
    val liststr = settings.getString(key, "")
    try {
        return string2SceneList(liststr!!)
    } catch (e: Exception) {
    }

    return null
}

fun SceneList2String(hashmap: HashMap<String, Int>): String {
    // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
    val byteArrayOutputStream = ByteArrayOutputStream()
    // 然后将得到的字符数据装载到ObjectOutputStream
    val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream)
    // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
    objectOutputStream.writeObject(hashmap)
    // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
    val SceneListString = String(Base64.encode(
            byteArrayOutputStream.toByteArray(), Base64.DEFAULT))
    // 关闭objectOutputStream
    objectOutputStream.close()
    return SceneListString
}

private fun string2SceneList(SceneListString: String): HashMap<String, Int> {
    val mobileBytes = Base64.decode(SceneListString.toByteArray(), Base64.DEFAULT)
    val byteArrayInputStream = ByteArrayInputStream(mobileBytes)
    val objectInputStream = ObjectInputStream(byteArrayInputStream)
    val sceneList = objectInputStream.readObject() as HashMap<String, Int>
    objectInputStream.close()
    return sceneList
}
