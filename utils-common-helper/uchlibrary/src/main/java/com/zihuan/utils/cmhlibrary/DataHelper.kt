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

/**
 * 当前数是否大于九
 */
fun Int.lessThanNine() = this <= 9

/**
 * 如果小于九在前面添0
 */
fun Int.lessNineAddZero() = if (this.lessThanNine()) "0$this" else this.toString()

/**
 * 返回第一个非空的字符
 * 适用于有多个参数,但不知道哪个参数不为空
 */
fun StringDetermineEmpty(vararg args: String): String {
    args.forEach {
        if (!it.isNullOrBlank()) return it
    }
    return ""
}

/**
 * 返回第一个非零的数字
 */
fun IntNotZero(vararg args: Int): Int {
    args.forEach {
        if (it > 0) return it
    }
    return 0
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

