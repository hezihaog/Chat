package com.zh.android.chat.mine.http

import com.lzy.okgo.OkGo
import com.lzy.okgo.request.GetRequest
import com.lzy.okgo.request.PostRequest
import com.lzy.okrx2.adapter.ObservableBody
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.genericGsonType
import com.zh.android.base.ext.toJson
import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.ModelConvert
import com.zh.android.chat.service.module.mine.model.User
import io.reactivex.Observable
import java.io.File

/**
 * @author wally
 * @date 2020/08/27
 * 我的模块请求封装
 */
class MineRequester {
    companion object {
        /**
         * 根据用户Id获取用户信息
         * @param userId 用户Id
         */
        fun getUserInfo(
            tag: String,
            userId: String
        ): Observable<HttpModel<User>> {
            val type = genericGsonType<HttpModel<User>>()
            val request: GetRequest<HttpModel<User>> = OkGo.get(ApiUrl.GET_USER_INFO)
            return request.tag(tag)
                .params("userId", userId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 更新昵称
         * @param userId 用户Id
         * @param newNickName 新的昵称
         */
        fun updateNickName(
            tag: String,
            userId: String,
            newNickName: String
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> = OkGo.post(ApiUrl.UPDATE_NICKNAME)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("id", userId)
                    put("nickname", newNickName)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 上传头像
         * @param userId 用户Id
         * @param avatarFile 新的头像File对象
         */
        fun uploadAvatar(
            tag: String,
            userId: String,
            avatarFile: File
        ): Observable<HttpModel<User>> {
            val type = genericGsonType<HttpModel<User>>()
            val request: PostRequest<HttpModel<User>> = OkGo.post(ApiUrl.UPLOAD_AVATAR)
            return request.tag(tag)
                .params("file", avatarFile)
                .params("userId", userId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }
    }
}