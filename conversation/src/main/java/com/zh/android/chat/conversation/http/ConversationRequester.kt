package com.zh.android.chat.conversation.http

import com.lzy.okgo.OkGo
import com.lzy.okgo.request.GetRequest
import com.lzy.okgo.request.PostRequest
import com.lzy.okrx2.adapter.ObservableBody
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.genericGsonType
import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.ModelConvert
import com.zh.android.base.http.PageModel
import com.zh.android.chat.service.module.conversation.model.ChatRecord
import com.zh.android.chat.service.module.conversation.model.Conversation
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/08/28
 * 会话模块 请求
 */
class ConversationRequester {
    companion object {
        /**
         * 获取自己和指定好友之间的聊天记录
         * @param userId 我的用户Id
         * @param friendUserId 好友的用户Id
         */
        fun getChatRecordList(
            tag: String,
            userId: String,
            friendUserId: String,
            pageNum: Int,
            pageSize: Int
        ): Observable<HttpModel<PageModel<ChatRecord>>> {
            val type = genericGsonType<HttpModel<PageModel<ChatRecord>>>()
            val request: GetRequest<HttpModel<PageModel<ChatRecord>>> =
                OkGo.get(ApiUrl.GET_CHAT_RECORD_LIST)
            return request.tag(tag)
                .params("userId", userId)
                .params("friendUserId", friendUserId)
                .params("pageNum", pageNum)
                .params("pageSize", pageSize)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取用户的所有会话
         */
        fun getAllConversation(
            tag: String,
            userId: String
        ): Observable<HttpModel<List<Conversation>>> {
            val type = genericGsonType<HttpModel<List<Conversation>>>()
            val request: GetRequest<HttpModel<List<Conversation>>> =
                OkGo.get(ApiUrl.GET_ALL_CONVERSATION)
            return request.tag(tag)
                .params("userId", userId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 删除一条聊天记录
         * @param recordId 聊天记录Id
         */
        fun deleteChatRecord(
            tag: String,
            recordId: String
        ): Observable<HttpModel<Boolean>> {
            val type = genericGsonType<HttpModel<Boolean>>()
            val request: PostRequest<HttpModel<Boolean>> =
                OkGo.post(ApiUrl.DELETE_CHAT_RECORD)
            return request.tag(tag)
                .params("recordId", recordId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 删除和指定好友的整个会话
         */
        fun deleteConversation(
            tag: String,
            userId: String,
            friendUserId: String
        ): Observable<HttpModel<Boolean>> {
            val type = genericGsonType<HttpModel<Boolean>>()
            val request: PostRequest<HttpModel<Boolean>> =
                OkGo.post(ApiUrl.DELETE_CONVERSATION)
            return request.tag(tag)
                .params("userId", userId)
                .params("friendUserId", friendUserId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }
    }
}