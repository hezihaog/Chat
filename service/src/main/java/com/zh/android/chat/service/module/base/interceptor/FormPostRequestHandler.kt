package com.zh.android.chat.service.module.base.interceptor

import com.apkfuns.logutils.LogUtils
import com.lzy.okgo.request.base.ProgressRequestBody
import com.zh.android.base.constant.ApiUrl
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody
import org.joor.Reflect

/**
 * Package: me.wally.lvjiguide.http.processhandler
 * FileName: PostRequestProcessHandler
 * Date: on 2018/9/28  上午10:18
 * Auther: zihe
 * Descirbe:处理Post请求是表单提交，添加默认参数，如果是提交Json则无能为力了
 * Email: hezihao@linghit.com
 */
class FormPostRequestHandler : RequestProcessHandler {
    override fun isCanHandle(originRequest: Request): Boolean {
        val body = originRequest.body()
        if (body is FormBody) {
            return true
        } else if (body is ProgressRequestBody<*>) {
            return try {
                val newBody: RequestBody = Reflect.on(body).field("requestBody").get()
                newBody is FormBody
            } catch (e: Exception) {
                false
            }
        }
        return false
    }

    override fun process(originRequest: Request): Request {
        val loginService = getLoginService()
        val token = loginService?.getToken() ?: ""
        val userId = loginService?.getUserId() ?: ""
        val builder = FormBody.Builder()
        //原始Header
        val originHeaders = originRequest.headers()
        //增加公共Header
        val newHeaders = Headers.Builder()
            //先添加原有Header
            .addAll(originHeaders)
            .add(AppConstant.HttpParameter.PLATFORM, ApiUrl.PLATFORM).apply {
                //增加公共参数
                if (token.isNotBlank()) {
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
        val body: FormBody = when (val originBody = originRequest.body()) {
            is FormBody -> originBody
            is ProgressRequestBody<*> -> Reflect.on(originBody).field("requestBody").get()
            else -> throw IllegalArgumentException("body not is FormBody")
        }
        //将以前的参数添加
        for (i in 0 until body.size()) {
            builder.add(body.encodedName(i), body.encodedValue(i))
        }
//        run {
//            //增加公共参数
//            if (token.isNotBlank()) {
//                builder.add(AppConstant.HttpParameter.TOKEN, token)
//                Logger.d("FORM POST => 添加公共参数 -> ${AppConstant.HttpParameter.TOKEN} : $token")
//            }
//            if (username.isNotBlank()) {
//                builder.add(AppConstant.HttpParameter.USERNAME, username)
//                Logger.d("FORM POST => 添加公共参数 -> ${AppConstant.HttpParameter.USERNAME} : $username")
//            }
//            if (tenantId.isNotBlank()) {
//                builder.add(AppConstant.HttpParameter.TENANT_ID, tenantId)
//                Logger.d("FORM POST => 添加公共参数 -> ${AppConstant.HttpParameter.TENANT_ID} : $tenantId")
//            }
//        }
        val newBuilder = originRequest.newBuilder()
        //构造新的请求体
        return newBuilder
            .headers(newHeaders)
            .post(builder.build())
            .build()
    }
}