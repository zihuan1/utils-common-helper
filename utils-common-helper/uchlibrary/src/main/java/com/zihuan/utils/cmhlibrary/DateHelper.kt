package com.zihuan.utils.cmhlibrary

import android.text.TextUtils
import android.util.Log

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date

/**
 * 日期转换工具类
 */

/**
 * 时间转换为时间戳
 */
fun dateToStamp(time: String, type: String): String {
    var time = time
    var type = type
    if (TextUtils.isEmpty(type)) {
        type = "yyyy-MM-dd HH:mm:ss"
    }
    val simpleDateFormat = SimpleDateFormat(type)
    try {
        var date = simpleDateFormat.parse(time)
        val ts = date.time / 1000
        time = ts.toString()
    } catch (e: ParseException) {
        Logger("时间转换为时间戳异常 $e")
    }
    return time
}

/**
 *  时间戳转换为时间
 */
fun stampToDate(time: String, type: String): String {
    var type = type
    if (TextUtils.isEmpty(type)) {
        type = "yyyy-MM-dd"
    }
    if (TextUtils.isEmpty(time)) {
        return ""
    }
    var d = java.lang.Long.parseLong(time)
    val sdf = SimpleDateFormat(type)
    if (time.length == 10) {
        d *= 1000
    }
    val date = sdf.format(Date(d))
    //       Logger("data"+ date);
    return date + ""
}


/**
 * 时间戳距当前多长时间 (ddHHmmss)
 */
fun stampFormDate_dd_HH_mm_ss(argTime: String?): String? {
    if (argTime != null && argTime == "") {
        return ""
    }
    val time = java.lang.Long.parseLong(argTime!!)
    val mData = Date(time * 1000)
    val cal = Calendar.getInstance()   //年月日格式
    cal.time = mData
    var strTime: String? = null
    var interval = (System.currentTimeMillis() - mData.time) / 1000
    if (interval < 0)
        interval = 0
    val iMinute = 60
    val iHour = 60 * iMinute
    val iDay = 24 * iHour

    Logger("second$interval")
    strTime = when {
        interval.toInt() in 1..59 -> "" + interval.toInt() + "秒前"
        (interval / iMinute).toInt() in 1..59 -> "" + (interval / iMinute).toInt() + "分钟前"
        (interval / iHour).toInt() in 1..23 ->
            "" + (interval / iHour).toInt() + "小时前"
        (interval / iDay).toInt() in 1..1 -> "" + (interval / iDay).toInt() + "天前"
        else ->
            SimpleDateFormat("MM月dd日 HH:mm").format(Date(time))
    }
    return strTime
}

/**
 * 时间戳距当前多长时间 (yyyyMMddHHmmss)
 */
fun stampFormDateYY_MM_dd_HH_mm_ss(argTime: Long): String {
    val mData = Date(argTime)
    val formatter = SimpleDateFormat("HH:mm")
    val dateString = formatter.format(argTime)
    val cal = Calendar.getInstance()   //年月日格式
    cal.time = mData

    val strTime: String
    var interval = (System.currentTimeMillis() - mData.time) / 1000
    if (interval < 0)
        interval = 0
    val iMinute = 60
    val iHour = 60 * iMinute
    val iDay = 24 * iHour
    val iMonth = 30 * iDay
    val iYear = 365 * iMonth
    when {
        (interval / iYear).toInt() != 0 ->
            strTime = cal.get(Calendar.YEAR).toString() + "年" + cal.get(Calendar.MONTH) + "月" +
                    cal.get(Calendar.DAY_OF_MONTH) + "日" + dateString
        (interval / iMonth).toInt() != 0
        -> strTime =
            (cal.get(Calendar.MONTH) + 1).toString() + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日" + dateString
        (interval / iDay).toInt() != 0
        -> strTime = (cal.get(Calendar.MONTH) + 1).toString() + "月" +
                cal.get(Calendar.DAY_OF_MONTH) + "日" + dateString
        (interval / iHour).toInt() != 0
        -> strTime = if (interval / iHour >= 2) {
            (cal.get(Calendar.MONTH) + 1).toString() + "月" +
                    cal.get(Calendar.DAY_OF_MONTH) + "日" + dateString
        } else {
            (interval / iHour).toString() + "小时前"
        }
        (interval / iMinute).toInt() != 0 -> strTime = "" + (interval / iMinute).toInt() + "分钟前"
        else -> strTime = interval.toString() + "秒前"
    }

    return strTime
}

// 获得系统时间自定义类型
fun getSysTimeType(type: String): String {
    var type = type
    if (TextUtils.isEmpty(type)) {
        type = "yyyyMMddHHmmss"
    }
    val formatter = SimpleDateFormat(type)
    val curDate = Date(System.currentTimeMillis())//获取当前时间
    return formatter.format(curDate)
}

/**
 * 获取向后推迟的时间
 *
 * @param type "yyyy-MM-dd"
 * @param day  后几天
 * @return
 */
fun getAfterDay(time: String, type: String, day: Int): String {
    val formatter = SimpleDateFormat(type)
    //     Calendar类有一个方法add方法可以使用，例如calendar.add(Calendar.WEEK_OF_YEAR, -1);表示把时间向上推一周，
    //     calendar.add(Calendar.YEAR, -1);表示把时间向上推一年。
    val calendar = Calendar.getInstance()
    var date: Date? = null
    try {
        date = formatter.parse(time)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    calendar.time = date
    //        calendar.add(Calendar.WEEK_OF_YEAR, -1);
    calendar.add(Calendar.DAY_OF_MONTH, +day)
    date = calendar.time
    val str = formatter.format(date)
    Logger("向后" + day + "天" + "" + str)
    return str + ""
}

/**
 * 时间戳转换为带AM/PM的时间
 */
fun getDateAmPm(time: String, type: String): String {
    var type = type
    if (TextUtils.isEmpty(type)) {
        type = "yyyy-MM-dd"
    }

    if (TextUtils.isEmpty(time)) {
        return ""
    }
    val d = java.lang.Long.parseLong(time)
    val sdf = SimpleDateFormat(type)
    val data = Date(d * 1000)
    var date = sdf.format(data)
    date += if (data.hours < 12) {
        "am"
    } else {
        "pm"
    }
    //       Logger("data"+ date);

    return date + ""
}

/**
 * 获取当前日期是星期几
 *
 * @param time
 * @return 当前日期是星期几
 */
fun getWeekOfDate(time: Long): String {
    val weekDays = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
    val cal = Calendar.getInstance()
    cal.time = Date(time)
    val w = cal.get(Calendar.DAY_OF_WEEK) - 1
    return weekDays[w]
}

var topWeek: MutableList<String> = ArrayList()

/**
 * 获得上一周的数据 c 负数时向上一周 正数时间向下一周 0本周
 */
fun getTopWeek(c: Int): List<*> {
    val sf = SimpleDateFormat("dd")
    val cal = Calendar.getInstance()
    println("=========LastWeek Days=========")
    cal.add(Calendar.WEEK_OF_MONTH, c)
    for (i in 0..6) {
        cal.add(Calendar.DATE, -1 * cal.get(Calendar.DAY_OF_WEEK) + 2 + i)
        Logger("上一周" + sf.format(cal.time))
        topWeek.add(sf.format(cal.time) + "")
    }
    return topWeek
}

/**
 * 计算两个时间戳相隔多少天
 */
fun getApartDay(a: String, b: String): Int {

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val day1 = sdf.format(Date(java.lang.Long.parseLong(a) * 1000))
    val day2 = sdf.format(Date(java.lang.Long.parseLong(b) * 1000))
    var d1: Date? = null
    var d2: Date? = null
    try {
        d1 = sdf.parse(day1)
        d2 = sdf.parse(day2)
    } catch (e: ParseException) {
        Logger("异常$e")
    }

    val cal = Calendar.getInstance()
    cal.time = d1
    val time1 = cal.timeInMillis
    cal.time = d2
    val time2 = cal.timeInMillis
    val betweenDays = (time2 - time1) / (1000 * 3600 * 24)


    //        Log.e("betweenDays", betweenDays + "天");

    //        if(betweenDays == 0) {   //半天
    //            return "半";
    //        }

    //        return (Long.parseLong(b) - Long.parseLong(a)) / (86400000) + "";
    return Integer.parseInt(betweenDays.toString())
}

/**
 * 两个时间戳相差多少时分秒
 */
fun timeStampDiffHMS(time1: String, time2: String): String {
    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    try {
        val d1 = df.parse(time1)//当前时间

        val d2 = df.parse(time2)//过去时间

        val diff = d1.time - d2.time//这样得到的差值是微秒级别

        val days = diff / (1000 * 60 * 60 * 24)

        val hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)

        val minutes =
            (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60)

        val miao =
            (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000
        Logger("" + days + "天" + hours + "小时" + minutes + "分" + miao + "秒")
        Logger("diff $diff")
        return if (hours == 0L) {
            (+minutes).toString() + ":" + miao
        } else {
            "$hours:$minutes:$miao"
        }
    } catch (e: Exception) {

    }

    return "0"
}

/**
 * 计算两个时间戳相差多少秒
 */
fun timeStampDiffSecond(time1: String, time2: String): String {
    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    try {
        val d1 = df.parse(time1)//当前时间

        val d2 = df.parse(time2)//过去时间
        val diff: Long

        diff = d2.time - d1.time//这样得到的差值是微秒级别

        //            long year = diff / (1000 * 60 * 60 * 24 *365);

        Logger("" + diff)

        return diff.toString() + ""

    } catch (e: Exception) {
        Logger("Exception$e")
    }
    return "0"
}


/**
 * 获取今天开始的 0点 0 分
 *
 * @return
 */
fun GetToday(): Long {
    val strToday = getSysTimeType("yyyy年MM月dd日")
    val today = dateToStamp("$strToday 00:00:00", "yyyy-MM-dd HH:mm:ss").toLong()
    return today / 1000
}

/**
 * 获取今天 结束的 0点 0 分
 *
 * @return
 */
fun GetTomorrow(): Long {
    val today = GetToday()
    return today + 24 * 60 * 60
}

var DateUtilsDebug = false

fun Logger(logger: String) {
    if (DateUtilsDebug) {
        Log.e("DateUtil", logger)
    }
}
