package com.zh.android.chat.conversation.ws

import android.content.Context
import com.hule.dashi.websocket.WebSocketInfo
import com.zh.android.base.util.json.JsonProxy
import com.zh.android.chat.conversation.WebSocketAgent
import com.zh.android.chat.service.module.conversation.enums.ChatMsgType
import com.zh.android.chat.service.module.conversation.model.ChatRecord
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/08/28
 * 消息解析器
 */
class MsgParser(
    private val context: Context,
    /**
     * 连接地址
     */
    private val wsUrl: String,
    /**
     * 消息解析后分类进行回调
     */
    private val callback: OnReceiveMsgCallback
) {
    /**
     * 开始监听
     */
    fun listener(): Observable<WebSocketInfo> {
        //连接服务
        return WebSocketAgent.getRxWebSocket(context)
            .flatMap {
                it.get(wsUrl)
            }.flatMap {
                //解析文本消息
                parserTextMsg(it)
            }.flatMap {
                //解析图片消息
                parserImageMsg(it)
            }.flatMap {
                //解析语音消息
                parserVoiceMsg(it)
            }
    }

    /**
     * 解析文本消息
     */
    private fun parserTextMsg(
        info: WebSocketInfo
    ): Observable<WebSocketInfo> {
        return Observable.just(info)
            .flatMap {
                val json = it.stringMsg ?: ""
                if (json.isNotBlank()) {
                    //解析Json
                    val chatRecord =
                        JsonProxy.get().fromJson<ChatRecord>(json, ChatRecord::class.java)
                    if (chatRecord != null &&
                        chatRecord.type == ChatMsgType.TEXT.code &&
                        chatRecord.text != null
                    ) {
                        callback.onReceiveTextMsg(chatRecord)
                    }
                }
                Observable.just(it)
            }
    }

    /**
     * 解析图片消息
     */
    private fun parserImageMsg(
        info: WebSocketInfo
    ): Observable<WebSocketInfo> {
        return Observable.just(info)
            .flatMap {
                val json = it.stringMsg ?: ""
                if (json.isNotBlank()) {
                    //解析Json
                    val chatRecord =
                        JsonProxy.get().fromJson<ChatRecord>(json, ChatRecord::class.java)
                    if (chatRecord != null &&
                        chatRecord.type == ChatMsgType.IMAGE.code &&
                        chatRecord.image != null
                    ) {
                        callback.onReceiveImageMsg(chatRecord)
                    }
                }
                Observable.just(it)
            }
    }

    /**
     * 解析图片消息
     */
    private fun parserVoiceMsg(
        info: WebSocketInfo
    ): Observable<WebSocketInfo> {
        return Observable.just(info)
            .flatMap {
                val json = it.stringMsg ?: ""
                if (json.isNotBlank()) {
                    //解析Json
                    val chatRecord =
                        JsonProxy.get().fromJson<ChatRecord>(json, ChatRecord::class.java)
                    if (chatRecord != null &&
                        chatRecord.type == ChatMsgType.VOICE.code &&
                        chatRecord.voice != null
                    ) {
                        callback.onReceiveVoiceMsg(chatRecord)
                    }
                }
                Observable.just(it)
            }
    }

    interface OnReceiveMsgCallback {
        /**
         * 接收到文本消息
         */
        fun onReceiveTextMsg(chatRecord: ChatRecord)

        /**
         * 接收到图片消息
         */
        fun onReceiveImageMsg(chatRecord: ChatRecord)

        /**
         * 接收到语音消息
         */
        fun onReceiveVoiceMsg(chatRecord: ChatRecord)
    }
}