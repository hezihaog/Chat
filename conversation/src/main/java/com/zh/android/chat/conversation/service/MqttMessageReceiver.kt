package com.zh.android.chat.conversation.service

import com.zh.android.chat.conversation.model.Message

/**
 * @author wally
 * @date 2020/09/16
 * MQTT消息回调接口
 */
interface MqttMessageReceiver {
    /**
     * 接收到离线聊天消息
     */
    fun onReceiveOfflineChatMsg(model: Message)
}