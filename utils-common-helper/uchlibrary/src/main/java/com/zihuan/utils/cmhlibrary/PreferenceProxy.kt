package com.zihuan.utils.cmhlibrary

import android.content.Context
import android.widget.Toast
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceProxy<T>(val key: String, val default: T, val preName: String = "default")
    : ReadWriteProperty<Any?, T> {
    private val prefs by lazy {
        CommonContext.getSharedPreferences(preName, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(key,default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(key, value)
    }


//    private fun findPreference(key: String): T {
//        return with(prefs) {
//            when (default) {
//                is String -> getString(key, default)
//                is Int -> getInt(key, default)
//                is Float -> getFloat(key, default)
//                is Long -> getLong(key, default)
//                is Boolean -> getBoolean(key, default)
//                else -> Toast.makeText(CommonContext, "Preference不支持的类型$default", Toast.LENGTH_LONG).show()
//            }
//        } as T
//    }
//
//    private fun putPreference(key: String, value: T) {
//        prefs.edit().apply {
//            when (value) {
//                is String -> putString(key, value)
//                is Int -> putInt(key, value)
//                is Float -> putFloat(key, value)
//                is Long -> putLong(key, value)
//                is Boolean -> putBoolean(key, value)
//                else -> Toast.makeText(CommonContext, "Preference不支持的类型$value", Toast.LENGTH_LONG).show()
//            }
//        }.apply()
//    }

}