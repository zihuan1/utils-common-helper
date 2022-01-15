package com.zihuan.demo

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.zihuan.utils.cmhlibrary.*
import kotlinx.android.synthetic.main.activity_main.*


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
        var name = findPreference("name", "")
        Log.e("输出name", "getCommonPreference =$name")
        var name1 by PreferenceProxy("name", "")
        Log.e("输出name1", "PreferenceProxy =$name1")
        val mo = "123456"
        val mo1 = 123456L
        val mo2 = 123456.2
        mo1.savePreference("123")
        mo2.savePreference("name")
        mo.savePreference("mobile")
        val ceshi = "ceshi2"
        "多文件存储测试".savePreference("test", ceshi)
        Log.e("缓存文件", getDiskCacheFile())
        Log.e("缓存数据", getDiskCacheData())
        Log.e("缓存文件", getDiskCacheFile(true))
        Log.e("缓存数据", getDiskCacheData(true))
        val select = true
        select.savePreference("select")
        Log.e("转换", "${10.dp}")
        Log.e("转换", "${10f.dp}")
        val path = Environment.getExternalStorageDirectory().absolutePath
        Log.e("测试时间", "测试" + formatTime(31000))
        ivScreenshots.setOnClickListener {
            llMain.toPng(path, "${System.currentTimeMillis()}.png")
            toast("截图成功")
        }
        var mobile = findPreference("mobile", "")
        Log.e("输出mobile", "getCommonPreference =$mobile")
        removePreference("mobile")
        var mobile2 = findPreference("mobile", "")
        Log.e("删除后输出 mobile ", "mobile =$mobile2")
        Log.e("删除后输出其他默认文件值 name ", "name =${findPreference("name", "")}")
        clearPreference()
        Log.e("清除默认存储后获取 name ", "name =${findPreference("name", "")}")
        Log.e("清除默认文件后获取 ceshi2 ", "ceshi2 =${findPreference("test", "", ceshi)}")
        clearAllPreference()
        Log.e("清除所有文件后获取 ceshi2 ", "ceshi2 =${findPreference("test", "", ceshi)}")
        val s = "10.0153"
        Log.e("保留两位小数", s.keepDecimal())
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
        Log.e("宽高", "宽高$screenWidth  $screenHeight  ${screenHeight()}")
        Log.e("中文检测", "宽高".isChinese.toString())
        Log.e("英文检测", "Aa".isEnglish.toString())
        Log.e("标点检测", "._-_".isSymbol.toString())
        val action: (Int) -> Unit = {
            Log.e("测试", "" + it)
        }
        globalLoop(5, 1000, action) {
            Log.e("测试结束", "测试结束")
        }
        val entity = WCYXEntity()
        entity.clock_times = "666"
        entity.commonXinliName = "测试"
        entity.xinli_question_count = "777"
        val list = ArrayList<FinishedBotNames>()
        (0..5).forEach {
            val botName = FinishedBotNames()
            botName.botname = "测试$it"
            botName.finished_times = "${it + 1}"
            botName.last_finish_time = "${System.currentTimeMillis() + it}"
            list.add(botName)
        }
        entity.finished_botnames = list
        entity.last_finish_time = "888"
        entity.last_finished_botname = "999"
//        val gosn = Gson()
        val json =
            "{\"clock_times\":\"666\",\"commonXinliName\":\"测试\",\"xinli_question_count\":\"777\",\"finished_botnames\":[{\"botname\":\"测试0\",\"finished_times\":\"1\",\"last_finish_time\":\"1633834337258\"},{\"botname\":\"测试1\",\"finished_times\":\"2\",\"last_finish_time\":\"1633834337259\"},{\"botname\":\"测试2\",\"finished_times\":\"3\",\"last_finish_time\":\"1633834337260\"},{\"botname\":\"测试3\",\"finished_times\":\"4\",\"last_finish_time\":\"1633834337261\"},{\"botname\":\"测试4\",\"finished_times\":\"5\",\"last_finish_time\":\"1633834337262\"},{\"botname\":\"测试5\",\"finished_times\":\"6\",\"last_finish_time\":\"1633834337263\"}],\"last_finish_time\":\"888\",\"last_finished_botname\":\"999\"}"
        val gosn = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
//{"finished_botnames":[{"botname":"测试0","finished_times":"1","last_finish_time":"1633834273538"},{"botname":"测试1","finished_times":"2","last_finish_time":"1633834273539"},{"botname":"测试2","finished_times":"3","last_finish_time":"1633834273540"},{"botname":"测试3","finished_times":"4","last_finish_time":"1633834273541"},{"botname":"测试4","finished_times":"5","last_finish_time":"1633834273542"},{"botname":"测试5","finished_times":"6","last_finish_time":"1633834273543"}],"last_finish_time":"888","last_finished_botname":"999"}
        Log.e("GSON to Json", gosn.toJson(entity))
        val eneity1 = gosn.fromJson(json, WCYXEntity::class.java)
        Log.e("GSON to Entity", eneity1.toString())

    }

}
