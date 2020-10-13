package com.zh.android.chat.notice.service

import com.apkfuns.logutils.LogUtils
import com.zh.android.base.ext.genericGsonTypeToken
import com.zh.android.base.util.json.JsonProxy
import com.zh.android.chat.service.module.notice.model.NoticeModel
import com.zh.android.mqtt.MqttMessage
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 * @author wally
 * @date 2020/10/13
 * 通知模块的MQTT消息解析器
 */
object NoticeMessageParser {
    /**
     * 解析Mqtt消息，内部会对消息解析
     * @param receiver 消息接收者
     */
    fun parseMqttMessage(receiver: NoticeMqttMessageReceiver): ObservableTransformer<MqttMessage, MqttMessage> {
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
        receiver: NoticeMqttMessageReceiver
    ): Observable<MqttMessage> {
        try {
            LogUtils.d("解析Mqtt消息 - 开始: ${mqttMessage.message}")
            val model =
                JsonProxy.get().fromJson<NoticeModel>(
                    mqttMessage.message,
                    genericGsonTypeToken<NoticeModel>().type
                )
            if (model != null) {
                receiver.onReceiveNoticeMsg(model)
                LogUtils.d("解析Mqtt消息 - 成功")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.d("解析Mqtt消息 - 失败")
        }
        return Observable.just(mqttMessage)
    }
}