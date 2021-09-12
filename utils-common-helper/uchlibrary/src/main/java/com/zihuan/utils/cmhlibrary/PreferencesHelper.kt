package com.zihuan.utils.cmhlibrary

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.widget.Toast
import org.jetbrains.annotations.Nullable
import java.io.*


/**
 * 如果不想用代理的方式存取值,可以用快捷方法
 * 存储工作是异步的
 */

private val mPreferenceList = HashMap<String, SharedPreferences>()

/**
 * 存储文件默认名称
 */
var SHARE_PREFS_DEFAULT_NAME = "commonDefaultName"

@Deprecated(
    "直接用SHARE_PREFERENCE_DEFAULT_NAME变量",
    ReplaceWith("SHARE_PREFERENCE_DEFAULT_NAME = name")
)
fun setPreferencesName(@Nullable name: String) {
    SHARE_PREFS_DEFAULT_NAME = name
}

fun getShare(name: String = SHARE_PREFS_DEFAULT_NAME): SharedPreferences {
    if (!mPreferenceList.containsKey(name)) {
        val share = CommonContext.getSharedPreferences(name, Context.MODE_PRIVATE)
        mPreferenceList[name] = share
    }
    return mPreferenceList[name]!!
}

/**
 * 存储值
 * Double类型的数据会自动转换成String类型存储
 */
fun Number.savePreference(key: String, name: String = SHARE_PREFS_DEFAULT_NAME) {
    putPreference(key, this, name)
}

fun String.savePreference(key: String, name: String = SHARE_PREFS_DEFAULT_NAME) {
    putPreference(key, this, name)
}

fun Boolean.savePreference(key: String, name: String = SHARE_PREFS_DEFAULT_NAME) {
    putPreference(key, this, name)
}

/**
 * 快速的获取值
 * var value=findPreference()
 */

fun <T : Any> findPreference(key: String, defValue: T, name: String = SHARE_PREFS_DEFAULT_NAME): T {
    return getPreference(key, defValue, name)
}

internal fun <T> putPreference(key: String, value: T, name: String = SHARE_PREFS_DEFAULT_NAME) {
    val prefs = getShare(name)
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

internal fun <T> getPreference(
    key: String,
    defValue: T,
    name: String = SHARE_PREFS_DEFAULT_NAME
): T {
    val prefs = getShare(name)
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
 * 清除默认文件数据
 */
fun clearPreference(name: String = SHARE_PREFS_DEFAULT_NAME) {
    val prefs = getShare(name)
    prefs.edit().clear().commit()
}

/**
 * 清除所有文件数据
 */
fun clearAllPreference() {
    mPreferenceList.forEach { it.value.edit().clear().commit() }
}

/**
 * 删除单条数据
 */
fun removePreference(vararg key: String, name: String = SHARE_PREFS_DEFAULT_NAME) {
    val prefs = getShare(name)
    key.forEach {
        prefs.edit().remove(it).commit()
    }
}

//    存储集合
fun putHashMap(
    key: String,
    map: HashMap<String, Int>,
    name: String = SHARE_PREFS_DEFAULT_NAME
): Boolean {
    val prefs = getShare(name)
    val editor = prefs.edit()
    try {
        editor.putString(key, sceneList2String(map))
    } catch (e: IOException) {
        toast("存储Map错误$e")
    }

    return editor.commit()
}

//
fun getHashMap(key: String, name: String = SHARE_PREFS_DEFAULT_NAME): HashMap<String, Int>? {
    val prefs = getShare(name)
    val liststr = prefs.getString(key, "")
    try {
        return string2SceneList(liststr!!)
    } catch (e: Exception) {
    }

    return null
}

private fun sceneList2String(hashmap: HashMap<String, Int>): String {
    // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
    val byteArrayOutputStream = ByteArrayOutputStream()
    // 然后将得到的字符数据装载到ObjectOutputStream
    val objectOutputStream = ObjectOutputStream(
        byteArrayOutputStream
    )
    // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
    objectOutputStream.writeObject(hashmap)
    // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
    val sceneListString = String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT))
    // 关闭objectOutputStream
    objectOutputStream.close()
    return sceneListString
}

private fun string2SceneList(SceneListString: String): HashMap<String, Int> {
    val mobileBytes = Base64.decode(SceneListString.toByteArray(), Base64.DEFAULT)
    val byteArrayInputStream = ByteArrayInputStream(mobileBytes)
    val objectInputStream = ObjectInputStream(byteArrayInputStream)
    val sceneList = objectInputStream.readObject() as HashMap<String, Int>
    objectInputStream.close()
    return sceneList
}
