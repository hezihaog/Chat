package com.zh.android.chat.conversation.http

import com.hule.dashi.websocket.RxWebSocket
import com.zh.android.base.http.HttpModel
import com.zh.android.chat.service.module.conversation.model.ChatRecord
import com.zh.android.chat.service.module.conversation.model.Conversation
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/08/28
 * 会话模块 请求
 */
class ConversationPresenter {
    companion object {
        private val TAG = ConversationPresenter::class.java.name
    }

    /**
     * 获取自己和指定好友之间的聊天记录
     * @param userId 我的用户Id
     * @param friendUserId 好友的用户Id
     */
    fun getChatRecordList(
        userId: String,
        friendUserId: String
    ): Observable<HttpModel<List<ChatRecord>>> {
        return ConversationRequester.getChatRecordList(TAG, userId, friendUserId)
    }

    /**
     * 获取用户的所有会话
     */
    fun getAllConversation(
        userId: String
    ): Observable<HttpModel<List<Conversation>>> {
        return ConversationRequester.getAllConversation(TAG, userId)
    }

    /**
     * 绑定连接
     * @param userId 用户Id
     */
    fun bindConnection(
        webSocket: RxWebSocket,
        wsUrl: String,
        userId: String
    ): Observable<Boolean> {
        return WsRequester.bindConnection(webSocket, wsUrl, userId)
    }

    /**
     * 发送文本消息
     * @param text 文本
     */
    fun sendTextMsg(
        webSocket: RxWebSocket,
        wsUrl: String,
        userId: String,
        friendId: String,
        text: String
    ): Observable<ChatRecord> {
        return WsRequester.sendTextMsg(webSocket, wsUrl, userId, friendId, text)
    }

    /**
     * 将消息已读
     * @param recordId 消息记录Id
     */
    fun readMsg(
        webSocket: RxWebSocket,
        wsUrl: String,
        recordId: String
    ): Observable<Boolean> {
        return WsRequester.readMsg(webSocket, wsUrl, recordId)
    }

    /**
     * 发送心跳
     */
    fun sendHeartBeatMsg(
        webSocket: RxWebSocket,
        wsUrl: String
    ): Observable<Boolean> {
        return WsRequester.sendHeartBeatMsg(webSocket, wsUrl)
    }
}