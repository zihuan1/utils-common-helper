package com.zihuan.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zihuan.utils.cmhlibrary.isEmptyExtend
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Field
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_1.setOnClickListener {
            var a = tv_3.text.toString()
            a.isEmptyExtend {
                Log.e("输出1", "测试$a")
                return@setOnClickListener
            }
            Log.e("输出2", "测试$a")
        }
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
