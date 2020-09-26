package com.zh.android.chat.conversation.http

import com.hule.dashi.websocket.RxWebSocket
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.util.json.JsonProxy
import com.zh.android.chat.conversation.enums.MessageType
import com.zh.android.chat.conversation.ws.model.BindChatModel
import com.zh.android.chat.conversation.ws.model.ReadMsgModel
import com.zh.android.chat.service.module.conversation.enums.ChatMsgType
import com.zh.android.chat.service.module.conversation.model.ChatMsg
import com.zh.android.chat.service.module.conversation.model.Message
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import java.util.concurrent.TimeUnit

/**
 * @author wally
 * @date 2020/08/28
 * WebSocket请求
 */
class WsRequester {
    companion object {
        /**
         * 绑定连接
         * @param userId 用户Id
         */
        fun bindConnection(
            webSocket: RxWebSocket,
            wsUrl: String,
            userId: String
        ): Observable<Boolean> {
            return Observable.create(ObservableOnSubscribe<String> {
                val msg = Message(
                    MessageType.CONNECTION.code,
                    ext = JsonProxy.get().toJson(
                        BindChatModel(
                            userId
                        )
                    )
                )
                val json = JsonProxy.get().toJson(msg)
                it.onNext(json)
            }).flatMap {
                webSocket.send(wsUrl, it)
            }
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
            return Observable.create(ObservableOnSubscribe<String> {
                val msg = Message(
                    MessageType.SEND.code,
                    chatMsg = ChatMsg(
                        ChatMsgType.TEXT.code,
                        userId,
                        friendId,
                        textContent = text
                    )
                )
                val json = JsonProxy.get().toJson(msg)
                it.onNext(json)
            }).flatMap {
                webSocket.send(wsUrl, it)
            }
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
            return Observable.create(ObservableOnSubscribe<String> {
                val msg = Message(
                    MessageType.SEND.code,
                    chatMsg = ChatMsg(
                        ChatMsgType.IMAGE.code,
                        userId,
                        friendId,
                        image = image
                    )
                )
                val json = JsonProxy.get().toJson(msg)
                it.onNext(json)
            }).flatMap {
                webSocket.send(wsUrl, it)
            }
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
            return Observable.create(ObservableOnSubscribe<String> {
                val msg = Message(
                    MessageType.SEND.code,
                    chatMsg = ChatMsg(
                        ChatMsgType.VOICE.code,
                        userId,
                        friendId,
                        mediaSrc = mediaSrc,
                        mediaTime = mediaTime
                    )
                )
                val json = JsonProxy.get().toJson(msg)
                it.onNext(json)
            }).flatMap {
                webSocket.send(wsUrl, it)
            }
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
            return Observable.create(ObservableOnSubscribe<String> {
                val msg = Message(
                    MessageType.READ_MSG.code,
                    ext = JsonProxy.get().toJson(
                        ReadMsgModel(
                            recordId
                        )
                    )
                )
                val json = JsonProxy.get().toJson(msg)
                it.onNext(json)
            }).flatMap {
                webSocket.send(wsUrl, it)
            }
        }

        /**
         * 发送心跳
         */
        fun sendHeartBeatMsg(
            webSocket: RxWebSocket,
            wsUrl: String
        ): Observable<Boolean> {
            return webSocket.heartBeat(wsUrl, ApiUrl.WS_HE_INTERVAL_TIME, TimeUnit.SECONDS) {
                val msg = Message(MessageType.HEAR_BEAT.code, null, null)
                JsonProxy.get().toJson(msg)
            }
        }
    }
}