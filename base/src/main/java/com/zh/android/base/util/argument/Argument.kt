package com.linghit.base.util.argument

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * <b>Package:</b> com.linghit.base.util <br>
 * <b>Create Date:</b> 2019-07-03  16:42 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */

/**
 * 拓展Activity获取方法
 */
fun <T> Activity.bindArgument(name: String, default: T): Argument<T> {
    return Argument(name, default) {
        this.intent?.extras ?: Bundle()
    }
}

fun <T> androidx.fragment.app.Fragment.bindArgument(name: String, default: T): Argument<T> {
    return Argument(name, default) {
        this.arguments ?: Bundle()
    }
}

/**
 * 属性代理，代理到Bundle上
 * @param name 属性名
 * @param default 默认值
 * @param block 获取Bundle对象的闭包
 */
class Argument<T>(private val name: String, private val default: T, private val block: () -> Bundle) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = findArgument(name, default)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = putArgument(name, value)

    @Suppress("UNCHECKED_CAST")
    private fun <U> findArgument(name: String, default: U): U = with(this.block()) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)!!
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            is Serializable -> getSerializable(name) ?: default
            is Parcelable -> getParcelable(name) ?: default
            else -> throw IllegalArgumentException("This type can be saved into Argument")
        }
        res as U
    }

    private fun <U> putArgument(name: String, value: U) = with(this.block()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            is Serializable -> putSerializable(name, value)
            is Parcelable -> putParcelable(name, value)
            else -> throw IllegalArgumentException("This type can be saved into Argument")
        }
    }
}