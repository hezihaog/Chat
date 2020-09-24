package com.zh.android.chat.service.module.base.interceptor

import com.apkfuns.logutils.LogUtils
import com.zh.android.base.constant.ApiUrl
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import okhttp3.Request
import okhttp3.RequestBody

/**
 * <b>Package:</b> com.tongwei.smarttoilet.service.base.interceptor <br>
 * <b>Create Date:</b> 2019-09-06  13:17 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> Json请求方式处理器 <br>
 */
class JsonPostRequestHandler : RequestProcessHandler {
    override fun isCanHandle(originRequest: Request): Boolean {
        return originRequest.body() is RequestBody
    }

    override fun process(originRequest: Request): Request {
        //原始Header
        val originHeaders = originRequest.headers()
        val newHeaders = originRequest.headers().newBuilder().apply {
            //增加公共参数
            val loginService = getLoginService()
            val token = loginService?.getToken() ?: ""
            val userId = loginService?.getUserId() ?: ""
            //平台标识
            add(AppConstant.HttpParameter.PLATFORM, ApiUrl.PLATFORM)
            //Token令牌
            if (token.isNotBlank()) {
                val key = AppConstant.HttpParameter.TOKEN
                if (originHeaders.get(key).isNullOrBlank()) {
                    add(key, token)
                    LogUtils.d("JSON POST => 添加公共参数 -> $key : $token")
                }
            }
            //用户Id
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