package com.zh.android.chat.conversation.ws

import android.content.Context
import com.hule.dashi.websocket.WebSocketInfo
import com.zh.android.base.util.json.JsonProxy
import com.zh.android.chat.conversation.WebSocketAgent
import com.zh.android.chat.conversation.enums.MessageType
import com.zh.android.chat.service.module.conversation.model.ChatRecord
import com.zh.android.chat.service.module.conversation.model.Message
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
                    //解析json，判断消息类型
                    val message = JsonProxy.get().fromJson<Message>(json, Message::class.java)
                    if (message != null
                        && message.type == MessageType.SEND.code
                        && message.chatRecord != null
                    ) {
                        callback.onReceiveTextMsg(message.chatRecord!!)
                    }
                }
                Observable.just(it)
            }
    }

    interface OnReceiveMsgCallback {
        /**
         * 接收到文本消息
         */
        fun onReceiveTextMsg(record: ChatRecord)
    }
}