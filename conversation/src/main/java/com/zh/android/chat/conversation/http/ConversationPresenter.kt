package com.zh.android.chat.conversation.http

import com.hule.dashi.websocket.RxWebSocket
import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.PageModel
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
        friendUserId: String,
        pageNum: Int,
        pageSize: Int
    ): Observable<HttpModel<PageModel<ChatRecord>>> {
        return ConversationRequester.getChatRecordList(TAG, userId, friendUserId, pageNum, pageSize)
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
     * 删除一条聊天记录
     * @param recordId 聊天记录Id
     */
    fun deleteChatRecord(
        recordId: String
    ): Observable<HttpModel<Boolean>> {
        return ConversationRequester.deleteChatRecord(TAG, recordId)
    }

    /**
     * 删除和指定好友的整个会话
     */
    fun deleteConversation(
        userId: String,
        friendUserId: String
    ): Observable<HttpModel<Boolean>> {
        return ConversationRequester.deleteConversation(TAG, userId, friendUserId)
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
    ): Observable<Boolean> {
        return WsRequester.sendTextMsg(webSocket, wsUrl, userId, friendId, text)
    }

    /**
     * 发送图片消息
     */
    fun sendImageMsg(
        webSocket: RxWebSocket,
        wsUrl: String,
        userId: String,
        friendId: String,
        image: String
    ): Observable<Boolean> {
        return WsRequester.sendImageMsg(webSocket, wsUrl, userId, friendId, image)
    }

    /**
     * 发送语音消息
     * @param mediaSrc 音频文件路径
     * @param mediaTime 音频文件的时长
     */
    fun sendVoiceMsg(
        webSocket: RxWebSocket,
        wsUrl: String,
        userId: String,
        friendId: String,
        mediaSrc: String,
        mediaTime: Int
    ): Observable<Boolean> {
        return WsRequester.sendVoiceMsg(webSocket, wsUrl, userId, friendId, mediaSrc, mediaTime)
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