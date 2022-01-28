package com.zihuan.utils.cmhlibrary

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceProxy<T>(private val key: String, private val default: T) : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getPreference(key,default)
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