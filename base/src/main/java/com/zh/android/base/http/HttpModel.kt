package com.zh.android.base.http

import android.text.TextUtils
import java.io.Serializable

/**
 * Project: ${file_name}<br></br>
 * Create Date: 2018/2/1 16:50<br></br>
 * Author: zixin<br></br>
 * Description: 接口返回包裹类，以后所有接口返回的类麻烦用这个类包裹一层<br></br>
 */
open class HttpModel<T> : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }

    /**
     * 是否成功
     */
    var success: Boolean = false

    /**
     * 附带消息
     */
    var message: String? = null

    /**
     * 结果
     */
    open var result: T? = null

    fun isMsgNull(): Boolean {
        return TextUtils.isEmpty(message)
    }

    fun success(): Boolean {
        return success
    }

    fun notSuccess(): Boolean {
        return !success()
    }

    fun errMsg(): String {
        return "$message"
    }

    override fun toString(): String {
        return "HttpModel(success=$success, message=$message, result=$result)"
    }
}