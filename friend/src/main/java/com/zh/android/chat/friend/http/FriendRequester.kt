package com.zh.android.chat.friend.http

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

/**
 * @author wally
 * @date 2020/08/27
 * 好友模块 请求封装
 */
class FriendRequester {
    companion object {
        /**
         * 查询用户的好友列表
         * @param userId 用户Id
         */
        fun getUserFriendList(
            tag: String,
            userId: String
        ): Observable<HttpModel<List<User>>> {
            val type = genericGsonType<HttpModel<List<User>>>()
            val request: GetRequest<HttpModel<List<User>>> =
                OkGo.get(ApiUrl.GET_USER_FRIEND_LIST)
            return request.tag(tag)
                .params("userId", userId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 根据用户名搜索用户信息
         * @param username 用户名
         */
        fun findUserByUsername(
            tag: String,
            username: String
        ): Observable<HttpModel<User>> {
            val type = genericGsonType<HttpModel<User>>()
            val request: GetRequest<HttpModel<User>> = OkGo.get(ApiUrl.FIND_BY_USERNAME)
            return request.tag(tag)
                .params("friendUsername", username)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 发送好友请求
         * @param fromUserId 发送请求的用户Id
         * @param toUserId 要添加的用户Id
         */
        fun sendFriendRequest(
            tag: String,
            fromUserId: String,
            toUserId: String
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> = OkGo.post(ApiUrl.SEND_FRIEND_REQUEST)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, String>().apply {
                    put("fromUserid", fromUserId)
                    put("toUserid", toUserId)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }
    }
}