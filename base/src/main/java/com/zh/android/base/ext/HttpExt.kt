package com.zh.android.base.ext

import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.request.GetRequest
import com.zh.android.base.R
import com.zh.android.base.http.HttpModel


/**
 * <b>Package:</b> com.linghit.base.ext <br>
 * <b>Create Date:</b> 2019-07-01  17:10 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> Http相关拓展 <br>
 */

/**
 * 处理HttpCode
 */
@JvmOverloads
fun handlerErrorCode(
    httpModel: HttpModel<*>?
): Boolean {
    return checkHttpResponse(httpModel, {
        showRequestError()
    }, {
        toast(it)
    })
}

/**
 * 检查响应是否成功
 * @param noMsgErrorBlock 没有Msg的异常回调
 * @param msgErrorBlock 有Msg的异常回调
 */
@JvmOverloads
fun checkHttpResponse(
    httpModel: HttpModel<*>?,
    noMsgErrorBlock: (() -> Unit)? = null,
    msgErrorBlock: ((msg: String) -> Unit)? = null
): Boolean {
    if (httpModel == null) {
        noMsgErrorBlock?.invoke()
        return false
    } else {
        if (httpModel.notSuccess()) {
            if (httpModel.message != null) {
                msgErrorBlock?.invoke(httpModel.message!!)
            } else {
                noMsgErrorBlock?.invoke()
            }
            return false
        }
        return true
    }
}

/**
 * 显示请求失败提示
 */
fun showRequestError() {
    toastLong(R.string.base_request_error_tip_msg)
}

/**
 * 显示请求的数据有误提示
 */
fun showRequestDataWrong() {
    toastLong(R.string.base_request_data_is_wrong)
}

/**
 * 设置缓存
 * @param mode 缓存模式
 * @param key 缓存Key
 */
fun <R> GetRequest<R>.useCache(mode: CacheMode? = null, key: String? = null): GetRequest<R> {
    //缓存模式，默认先使用缓存，再请求网络
    cacheMode(
        mode ?: CacheMode.FIRST_CACHE_THEN_REQUEST
    )
    //缓存Key
    cacheKey(
        if (key.isNullOrBlank()) {
            url + params.toString()
        } else {
            key
        }
    )
    return this
}