package com.zh.android.base.ext

import com.zh.android.base.R
import com.zh.android.base.http.HttpModel
import com.zh.android.base.util.net.NetManager


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
    httpModel: HttpModel<*>?, noNetworkBlock: (() -> Unit) = {
        toast(R.string.base_request_no_network)
    }
): Boolean {
    return checkHttpResponse(httpModel, {
        showRequestError()
    }, {
        toast(it)
    }, noNetworkBlock)
}

/**
 * 检查响应是否成功
 * @param noMsgErrorBlock 没有Msg的异常回调
 * @param msgErrorBlock 有Msg的异常回调
 * @param noNetworkBlock 请求时，当前设备没有网络时回调，有传时才进行网络情况判断
 */
@JvmOverloads
fun checkHttpResponse(
    httpModel: HttpModel<*>?,
    noMsgErrorBlock: (() -> Unit)? = null,
    msgErrorBlock: ((msg: String) -> Unit)? = null,
    noNetworkBlock: (() -> Unit)? = null
): Boolean {
    //有传没有网络回调时，才检查网络情况
    if (noNetworkBlock != null) {
        //当前设备，没有连接上网络
        if (!NetManager.isNetworkConnected(getAppContext())) {
            return false
        }
    }
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
    if (NetManager.isNetworkConnected(getAppContext())) {
        toastLong(R.string.base_request_error_tip_msg)
    } else {
        toastLong(R.string.base_request_no_network)
    }
}

/**
 * 显示请求的数据有误提示
 */
fun showRequestDataWrong() {
    toastLong(R.string.base_request_data_is_wrong)
}