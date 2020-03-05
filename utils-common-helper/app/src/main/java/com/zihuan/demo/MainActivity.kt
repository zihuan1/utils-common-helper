package com.zihuan.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zihuan.utils.cmhlibrary.CommonHelperCreate
import com.zihuan.utils.cmhlibrary.PreferenceProxy
import com.zihuan.utils.cmhlibrary.getCommonPreference
import com.zihuan.utils.cmhlibrary.savePreference
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Field
import java.util.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CommonHelperCreate.setContext(this)
        var email by PreferenceProxy("email", "")
        tv_1.setOnClickListener {
            email = tv_3.text.toString()
//            a.isEmptyExtend {
//                Log.e("输出1", "测试$a")
//                return@setOnClickListener
//            }
            Log.e("保存成功", "测试$email")
        }
        tv_2.setOnClickListener {
            Log.e("输出2", email)
        }
        "蛤蛤".savePreference("name")
        var name = getCommonPreference("name", "")
        Log.e("输出name", "getCommonPreference =$name")
        var name1 by PreferenceProxy("name", "")
        Log.e("输出name1", "PreferenceProxy =$name1")
        val mo = 123456
        val mo1 = 123456L
        val mo2 = 123456.2
        mo1.savePreference("123")
        mo2.savePreference("123")
        mo.savePreference("mobile")
        val select = true
        select.savePreference("select")
        var mobile = getCommonPreference("mobile", 0)
        Log.e("输出mobile", "getCommonPreference =$mobile")
//        var list = ArrayList<String>()
//        (0..100).forEach {
//            list.add("$it")
//        }
//        zrv_test.buildVerticalLayout(ReAdapter()).setData(list)
//
//        var test = Child()
//        val mClass = test.javaClass
////        var superObj = Child().javaClass
//        var superClass = mClass.newInstance().javaClass.superclass
//        var field = superClass.getDeclaredField("mStr")
//        //取消语法访问检查
//        field.isAccessible = true
////        var inputMore = field.get(superClass)
//        field.set(superClass, "222")
//        Log.e("反射", mClass.toString())
//
//        var ff = getFiledsInfo("com.zihuan.demo.Child")
//        ff.forEach {
//            Log.e("反射", it.name)
//            it.isAccessible = true
//            var a = it.get("mList")
//        }

    }

    fun getFiledsInfo(className: String): List<Field> {
        val list = ArrayList<Field>()
        val clazz = Class.forName(className)
        val superClazz = clazz.superclass
        if (superClazz != null) {
            val superFields = superClazz.declaredFields
            list.addAll(listOf<Field>(*superFields))
        }
        return list
    }

    fun <T> getFiledsInfo(clazz: Class<T>): List<Field> {
        val list = ArrayList<Field>()
        val superClazz = clazz.superclass
        if (superClazz != null) {
            val superFields = superClazz.declaredFields
            list.addAll(superFields)
        }
        return list
    }
}
