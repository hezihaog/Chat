package com.zh.android.chat.login.http

import com.lzy.okgo.OkGo
import com.lzy.okgo.request.GetRequest
import com.lzy.okgo.request.PostRequest
import com.lzy.okrx2.adapter.ObservableBody
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.genericGsonType
import com.zh.android.base.ext.toJson
import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.ModelConvert
import com.zh.android.chat.login.ext.md5Pwd
import com.zh.android.chat.login.model.LoginModel
import io.reactivex.Observable
import java.util.*

/**
 * @author wally
 * @date 2020/08/26
 * 登录模块请求封装
 */
class LoginRequester {
    companion object {
        /**
         * 登录
         * @param username 用户名
         * @param password 密码
         */
        fun login(
            tag: String,
            username: String,
            password: String
        ): Observable<HttpModel<LoginModel>> {
            val type = genericGsonType<HttpModel<LoginModel>>()
            val request: PostRequest<HttpModel<LoginModel>> = OkGo.post(ApiUrl.LOGIN)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("username", username)
                    put("password", password.md5Pwd)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 注册
         * @param username 用户名
         * @param password 密码
         */
        fun register(
            tag: String,
            username: String,
            password: String
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> = OkGo.post(ApiUrl.REGISTER)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("username", username)
                    put("password", password.md5Pwd)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取验证码
         * @param telephone 手机号
         */
        fun getAuthCode(
            tag: String,
            telephone: String
        ): Observable<HttpModel<String>> {
            val type = genericGsonType<HttpModel<String>>();
            val request: GetRequest<HttpModel<String>> = OkGo.get(ApiUrl.GET_AUTH_CODE)
            return request.tag(tag)
                .params("telephone", telephone)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 验证码登录
         * @param telephone 手机号
         * @param authCode  验证码
         */
        fun loginByAuthCode(
            tag: String,
            telephone: String,
            authCode: String
        ): Observable<HttpModel<LoginModel>> {
            val type = genericGsonType<HttpModel<LoginModel>>()
            val request: PostRequest<HttpModel<LoginModel>> = OkGo.post(ApiUrl.LOGIN_BY_AUTH_CODE)
            return request.tag(tag)
                .params("telephone", telephone)
                .params("authCode", authCode)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }
    }
}