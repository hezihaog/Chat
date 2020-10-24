package com.zh.android.base.ext

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import java.util.regex.Pattern

/**
 * <b>Package:</b> com.linghit.base.ext <br>
 * <b>Create Date:</b> 2019-06-29  09:29 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 字符串拓展 <br>
 */

/**
 * 拓展StringBuilder的清空
 */
fun StringBuilder.clear() {
    this.delete(0, this.length - 1)
}

/**
 * 删除StringBuilder的最后一个字符
 */
fun StringBuilder.deleteLast(lastChar: String) {
    if (lastChar == get(this.length - 1).toString()) {
        this.deleteCharAt(this.length - 1)
    }
}

/**
 * 拓展属性，获取StringBuilder最后一个字符
 */
var StringBuilder.lastChar: Char
    get() {
        return this[length - 1]
    }
    set(value) {
        this.setCharAt(length - 1, value)
    }

/**
 * 拓展StringBuffer的清空
 */
fun StringBuffer.clear() {
    this.delete(0, this.length - 1)
}

/**
 * 拓展属性，获取StringBuffer最后一个字符
 */
var StringBuffer.lastChar: Char
    get() {
        return this[length - 1]
    }
    set(value) {
        this.setCharAt(length - 1, value)
    }


/**
 * 集合内容拼接，每个元素之间用指定的分隔符分隔，默认分隔符是英文的逗号
 */
fun <E> MutableList<E>.listToString(separate: String = ","): String {
    val builder = StringBuilder()
    for (e in this) {
        builder.append(e.toString())
        builder.append(separate)
    }
    //删除最后一个分隔符
    builder.deleteLast(separate)
    return builder.toString()
}

/**
 * 判断字符串是否包含文字
 */
fun CharSequence.isContainChinese(): Boolean {
    val pattern = Pattern.compile("[\u4e00-\u9fa5]")
    val matcher = pattern.matcher(this)
    return matcher.find()
}

/**
 * 去掉前后空格和所有的空格
 */
fun CharSequence.trimAndRemoveAllBlank(): CharSequence {
    return this.trim().replace("\\s*".toRegex(), "")
}

/**
 * 去掉null和空串
 */
fun CharSequence?.filterNoNull(): CharSequence {
    return this?.replace("null".toRegex(), "") ?: ""
}

/**
 * 根据文件路径获取文件的图片Bitmap
 */
fun String.getFilePathImageBitmap(): Bitmap? {
    return BitmapFactory.decodeFile(this)
}

/**
 * 字符串是否不为null和空字符串
 */
fun CharSequence?.isNotNull(): Boolean {
    return !TextUtils.isEmpty(this)
}

/**
 * 字符串按逗号分隔
 */
fun String.splitByComma(): List<String> {
    return this.split(",")
}