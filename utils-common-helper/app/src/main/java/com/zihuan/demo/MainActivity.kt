package com.zihuan.demo

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.zihuan.utils.cmhlibrary.*
import com.zihuan.utils.cmhlibrary.FileUtils.stringMerge
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Field
import java.util.*


class MainActivity : FragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CommonHelperCreate.setContext(this)
        var email by PreferenceProxy("email", "")
        tv_1.setOnClickListener {
            email = et_path.text.toString()
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

        Log.e("转换", "${10.dp}")
        Log.e("转换", "${10f.dp}")
        val path = Environment.getExternalStorageDirectory().absolutePath

        ivScreenshots.setOnClickListener {
//            getbBitmap(llMain)
            //某些版本下似乎是需要在底部加边距才能完整截屏
            llMain.toPng(path, "${System.currentTimeMillis()}.png")
            ShowToast("截图成功")
        }
        var mobile = getCommonPreference("mobile", 0)
//        Log.e("输出mobile", "getCommonPreference =$mobile")
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
        et_path.setText(Environment.getExternalStorageDirectory().toString() + "/")
        shareFile.setOnClickListener {
            requestEasyPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) {
                var url = stringMerge(et_path.text.toString())
                Log.e("成功", "合并成功$url")
                Toast.makeText(this, "成功", Toast.LENGTH_LONG).show()
//                shareSystem(url)
            }
        }
    }

    /**
     * 截图listview
     */
    fun getbBitmap(listView: ViewGroup): Bitmap? {
        var h = 0
        var bitmap: Bitmap? = null
        // 获取listView实际高度
        for (i in 0 until listView.getChildCount()) {
            val view = listView.getChildAt(i)
            h += view.getHeight()
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(
            listView.getWidth(), h,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        listView.draw(canvas)
        val path = Environment.getExternalStorageDirectory().absolutePath

        bitmap.saveBitmapToSD(path, "test1.png", 100, 1f)
        ShowToast("成功")
        // 测试输出
//        var out: FileOutputStream? = null
//        try {
//            out = FileOutputStream("/sdcard/screen_test.png")
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        }
//        try {
//            if (null != out) {
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
//                out.flush()
//                out.close()
//            }
//        } catch (e: IOException) {
//            // TODO: handle exception
//        }
        return bitmap
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
