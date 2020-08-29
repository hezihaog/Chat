package com.zh.android.chat.conversation.http

import com.hule.dashi.websocket.RxWebSocket
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.util.json.JsonProxy
import com.zh.android.chat.conversation.enums.MessageType
import com.zh.android.chat.conversation.enums.ReadStatus
import com.zh.android.chat.conversation.model.ChatRecord
import com.zh.android.chat.conversation.model.Message
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
                val record = ChatRecord(
                    "",
                    userId,
                    "",
                    ReadStatus.UNREAD.code,
                    "",
                    ""
                )
                val msg = Message(
                    MessageType.CONNECTION.code, record, null
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
        ): Observable<ChatRecord> {
            return Observable.create(ObservableOnSubscribe<Pair<ChatRecord, String>> {
                val record = ChatRecord(
                    "",
                    userId,
                    friendId,
                    ReadStatus.UNREAD.code,
                    "",
                    text
                )
                val msg = Message(
                    MessageType.SEND.code, record, null
                )
                val json = JsonProxy.get().toJson(msg)
                it.onNext(Pair(record, json))
            }).flatMap { pair ->
                webSocket.send(wsUrl, pair.second).flatMap {
                    if (it) {
                        Observable.just(pair.first)
                    } else {
                        Observable.error(RuntimeException("发送失败"))
                    }
                }
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
                val record = ChatRecord(
                    recordId,
                    "",
                    "",
                    ReadStatus.READ.code,
                    "",
                    ""
                )
                val msg = Message(
                    MessageType.READ_MSG.code, record, null
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