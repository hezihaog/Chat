package com.zh.android.base.http

import android.text.TextUtils
import com.zh.android.base.constant.ApiUrl
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
     * 响应码，0为成功，非0为失败
     */
    var code: Int = 1

    /**
     * 附带消息
     */
    var message: String? = null

    /**
     * 结果
     */
    open var data: T? = null

    fun isMsgNull(): Boolean {
        return TextUtils.isEmpty(message)
    }

    fun code(): Int {
        return code
    }

    fun success(): Boolean {
        return code == ApiUrl.CODE_SUCCESS
    }

    fun notSuccess(): Boolean {
        return !success()
    }

    fun errMsg(): String {
        return "$message"
    }

    override fun toString(): String {
        return "HttpModel(code=$code, message=$message, result=$data)"
    }
}