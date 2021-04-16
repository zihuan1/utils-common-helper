package com.zihuan.utils.cmhlibrary

import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * 当前类包含了常用的基本数据类型操作
 *
 */


/**
 * 字符串非空扩展
 */
inline fun String.isNotEmpty(action: String.() -> Unit) = apply {
    if (!isNullOrBlank() && !isNullOrEmpty()) {
        action()
    }
}

/**
 * 字符串为空扩展
 */
inline fun String.isEmpty(action: String.() -> Unit) = apply {
    if (isNullOrEmpty() || isBlank()) {
        action()
    }
}

/**
 * true 扩展
 */
inline fun Boolean.isTrue(action: () -> Unit) = apply {
    if (this) action()
}

/**
 * false 扩展
 */
inline fun Boolean.isFalse(action: () -> Unit) = apply {
    if (!this) action()
}

/**
 * 是否是T对象,如果是的话返回自身,否则返回空
 */
inline fun <reified T> Any.instanceOf() = if (this is T) this else null

inline fun <reified T> Any.instanceOf(any: T) = this is T

/**
 * 三元运算
 */
inline fun <T> Boolean.unary(any: T, any2: T) = if (this) any else any2

inline fun <T> unary(any: T, any2: T, action: () -> Boolean) = action().unary(any, any2)

/**
 * 当前数是否大于九
 */
val Int.withNine: Boolean
    get() = toLong().withNine
val Long.withNine: Boolean
    get() = this > 9


val Int.withZero: Boolean
    get() = toLong().withZero

val Long.withZero: Boolean
    get() = this > 0

/**
 * 如果小于九在前面添0
 */
val Int.withNineZero: String
    get() = toLong().withNineZero

val Long.withNineZero: String
    get() = if (!withNine) "0$this" else toString()


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
fun String.keepDecimal(number: Int = 2) {
    val num = if (!isNullOrEmpty()) {
        this
    } else "0"
    "%.$number".format(this.toDouble())
}

/**
 * 判断当前字符是否为空,如果为空返回0,否则返回自身
 */
val String.notEmptyNumber: String
    get() = if (isNullOrEmpty() || isBlank()) "0" else this

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
val Int.dp: Int
    get() = toFloat().dp.toInt()

val Float.dp: Float
    get() = CommonContext.resources.displayMetrics.density * this

//sp转px
val Int.spx: Int
    get() = toFloat().spx.toInt()

val Float.spx: Float
    get() = (CommonContext.resources.displayMetrics.scaledDensity * this)


// px转dp
val Int.px: Int
    get() = toFloat().px.toInt()


val Float.px: Float
    get() = this / CommonContext.resources.displayMetrics.density


//px转sp
val Int.xsp: Int
    get() = toFloat().xsp.toInt()


val Float.xsp: Float
    get() = this / CommonContext.resources.displayMetrics.scaledDensity


//private fun <T : Number> T.compare(i: Number): Number {
//    return when (i) {
//        is Int -> i
//        else -> i as Long
//    }
//}


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
            if (null == entry.value) "" else entry.value.toString()
        ).append(if (iterator.hasNext()) "&" else "")
    }
    return sb.toString()
}

//是否为中文
val CharSequence.isChinese: Boolean
    get() = matcher(this, "[\u4e00-\u9fa5]+")

//是否为英文
val CharSequence.isEnglish: Boolean
    //    get() = matches(Regex("\\p{L}"))
    get() = matcher(this, "[a-zA-Z]+")

//是否为标点符号
val CharSequence.isSymbol: Boolean
    get() = matches(Regex("\\p{P}"))

fun matcher(text: CharSequence, regular: String): Boolean {
    val matcher: Matcher
    val pattern: Pattern = Pattern.compile(regular)
    matcher = pattern.matcher(text)
    return matcher.matches()
}