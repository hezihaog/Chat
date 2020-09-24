package com.zh.android.chat.service.module.base.interceptor

import com.apkfuns.logutils.LogUtils
import com.zh.android.base.constant.ApiUrl
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import okhttp3.Headers
import okhttp3.Request

/**
 * Package: me.wally.lvjiguide.http.processhandler
 * FileName: GetRequestProcessHandler
 * Date: on 2018/9/28  上午10:18
 * Auther: zihe
 * Descirbe:处理Get请求的表单提交
 * Email: hezihao@linghit.com
 */
class GetRequestHandler : RequestProcessHandler {
    override fun isCanHandle(originRequest: Request): Boolean {
        return "GET" == originRequest.method()
    }

    override fun process(originRequest: Request): Request {
        val loginService = getLoginService()
        val token = loginService?.getToken() ?: ""
        val userId = loginService?.getUserId() ?: ""
        //原始Header
        val originHeaders = originRequest.headers()
        //增加公共Header
        val newHeaders = Headers.Builder()
            //先添加原有Header
            .addAll(originHeaders).apply {
                //平台标识
                add(AppConstant.HttpParameter.PLATFORM, ApiUrl.PLATFORM)
                //增加公共参数
                if (token.isNotBlank()) {
                    //调用方没有加相同的公共参数时，才添加，避免在切换的业务场景时覆盖
                    val key = AppConstant.HttpParameter.TOKEN
                    if (originHeaders.get(key).isNullOrBlank()) {
                        add(key, token)
                        LogUtils.d("JSON POST => 添加公共参数 -> $key : $token")
                    }
                }
                if (userId.isNotBlank()) {
                    val key = AppConstant.HttpParameter.USER_ID
                    if (originHeaders.get(key).isNullOrBlank()) {
                        add(key, userId)
                        LogUtils.d("JSON POST => 添加公共参数 -> $key : $userId")
                    }
                }
            }
            .build()
        return originRequest.newBuilder()
            //设置Header
            .headers(newHeaders)
            .build()
    }
}