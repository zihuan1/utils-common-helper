package com.zihuan.utils.cmhlibrary

import java.util.regex.Pattern


/**
 * 当前类包含了常用的基本数据类型操作
 */


/**
 * 字符串非空扩展
 */
inline fun String.isNotEmptyExtend(action: String.() -> Unit) = apply {
    if (!isNullOrBlank() && !isNullOrEmpty()) {
        action()
    }
}

/**
 * 字符串为空扩展
 */
inline fun String.isEmptyExtend(action: String.() -> Unit) = apply {
    if (isNullOrEmpty() || isBlank()) {
        action()
    }
}

/**
 * true 扩展
 */
inline fun Boolean.trueExtend(action: () -> Unit) = apply {
    if (this) {
        action()
    }
}

/**
 * false 扩展
 */
inline fun Boolean.falseExtend(action: () -> Unit) = apply {
    if (!this) {
        action()
    }
}

/**
 * 是否是T对象,如果是的话返回自身,否则返回空
 */
inline fun <reified T> Any.instanceof(): T? {
    return if (this is T) {
        this
    } else null
}

/**
 * 三元运算
 */
inline fun <T> Boolean.threeUnary(any: T, any2: T): T {
    return if (this) {
        any
    } else any2
}

inline fun <T> threeUnary(any: T, any2: T, action: () -> Boolean): T {
    return if (action()) {
        any
    } else any2
}

/**
 * 当前数是否大于九
 */
fun Int.lessThanNine() = this <= 9

fun Long.lessThanNine() = this <= 9

fun Int.isEmpty() = this <= 0
fun Int.isNotEmpty() = this > 0

fun Long.isEmpty() = this <= 0
fun Long.isNotEmpty() = this > 0


/**
 * 反转布尔类型的集合
 */
fun MutableList<Boolean>.forEachReverseIndex(index: Int): MutableList<Boolean> {
    val listSize = size
    if (listSize < 2) return this
    val refer = if (index != 0) get(0) else get(index + 1)
    this.clear()
    for (pos in 0..listSize) {
        if (pos != index) {
            add(!refer)
        } else {
            add(refer)
        }
    }
    return this
}

//dp转px
var Int.dp: Int
    get() = this.toFloat().dp.toInt()
    set(value) {
    }

var Float.dp: Float
    get() = CommonContext.resources.displayMetrics.density * this
    set(value) {
    }

//sp转px
var Int.spx: Int
    get() = this.toFloat().spx.toInt()
    set(value) {}
var Float.spx: Float
    get() = (CommonContext.resources.displayMetrics.scaledDensity * this)
    set(value) {}

// px转dp
var Int.px: Int
    get() = this.toFloat().px.toInt()
    set(value) {}

var Float.px: Float
    get() = this / CommonContext.resources.displayMetrics.density
    set(value) {}

//px转sp
var Int.xsp: Int
    get() = this.toFloat().xsp.toInt()
    set(value) {}

var Float.xsp: Float
    get() = this / CommonContext.resources.displayMetrics.scaledDensity
    set(value) {}

//private fun <T : Number> T.compare(i: Number): Number {
//    return when (i) {
//        is Int -> i
//        else -> i as Long
//    }
//}

/**
 * 返回第一个非零的数字
 */
fun firstNotZero(vararg args: Int): Int {
    args.forEach {
        if (it > 0) return it
    }
    return 0
}

/**
 * 保留小数
 */
fun String.saveDecimal(number: Int = 2) {
    val num = if (!isNullOrEmpty()) {
        this
    } else "0"
    "%.$number".format(this.toDouble())
}

/**
 * 如果小于九在前面添0
 */
fun Int.lessNineAddZero() = if (this.lessThanNine()) "0$this" else this.toString()

/**
 * 返回第一个非空的字符
 * 适用于有多个参数,但不知道哪个参数不为空
 */
fun stringDetermineEmpty(vararg args: String): String {
    args.forEach {
        if (!it.isNullOrBlank()) return it
    }
    return ""
}


/**
 * 判断当前字符是否为空,如果为空返回0,否则返回自身
 */
fun String.getNotEmptyNumber() = if (isNullOrEmpty() || isBlank()) "0" else this


/**
 * 去除特殊字符或将所有中文标号替换为英文标号
 *
 * @param str
 * @return
 */
fun stringFilter(str: String): String {
    var str = str
    str = str.replace("【".toRegex(), "[").replace("】".toRegex(), "]")
        .replace("！".toRegex(), "!").replace("：".toRegex(), ":")// 替换中文标号
    val regEx = "[『』]" // 清除掉特殊字符
    val p = Pattern.compile(regEx)
    val m = p.matcher(str)
    return m.replaceAll("").trim { it <= ' ' }
}

/**
 * map转为字符串
 *
 * @param map
 * @return
 */
fun transMap2String(map: Map<*, *>): String {
    var entry: java.util.Map.Entry<*, *>
    val sb = StringBuffer()
    val iterator = map.entries.iterator()
    while (iterator.hasNext()) {
        entry = iterator.next() as java.util.Map.Entry<*, *>
        sb.append(entry.key.toString()).append("=").append(
            if (null == entry.value)
                ""
            else
                entry.value.toString()
        ).append(if (iterator.hasNext()) "&" else "")
    }
    return sb.toString()
}
