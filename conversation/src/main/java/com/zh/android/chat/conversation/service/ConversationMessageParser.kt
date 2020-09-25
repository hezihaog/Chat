package com.zh.android.chat.conversation.service

import com.apkfuns.logutils.LogUtils
import com.zh.android.base.ext.genericGsonTypeToken
import com.zh.android.base.util.json.JsonProxy
import com.zh.android.chat.service.module.conversation.enums.ChatMsgType
import com.zh.android.chat.service.module.conversation.model.ChatRecord
import com.zh.android.mqtt.MqttMessage
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 * <b>Package:</b> com.tongwei.smarttoilet.notice.service <br>
 * <b>Create Date:</b> 2019-09-09  14:22 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 通知消息解析器 <br>
 */
object ConversationMessageParser {
    /**
     * 解析Mqtt消息，内部会对消息解析
     * @param receiver 消息接收者
     */
    fun parseMqttMessage(receiver: MqttMessageReceiver): ObservableTransformer<MqttMessage, MqttMessage> {
        return ObservableTransformer { mqttMessage ->
            mqttMessage.flatMap { msg ->
                //消息处理
                Observable.just(msg).flatMap {
                    handleWorkOrderMsg(it, receiver)
                }
            }
        }
    }

    /**
     * 处理工单消息
     */
    @JvmStatic
    private fun handleWorkOrderMsg(
        mqttMessage: MqttMessage,
        receiver: MqttMessageReceiver
    ): Observable<MqttMessage> {
        try {
            LogUtils.d("解析Mqtt消息 - 开始: ${mqttMessage.message}")
            val model =
                JsonProxy.get().fromJson<ChatRecord>(
                    mqttMessage.message,
                    genericGsonTypeToken<ChatRecord>().type
                )
            if (model != null && ChatMsgType.isValid(model.type)) {
                receiver.onReceiveOfflineChatMsg(model)
                LogUtils.d("解析Mqtt消息 - 成功")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.d("解析Mqtt消息 - 失败")
        }
        return Observable.just(mqttMessage)
    }
}