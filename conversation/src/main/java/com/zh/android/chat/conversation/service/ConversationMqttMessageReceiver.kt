package com.zh.android.chat.conversation.service

import com.zh.android.chat.service.module.conversation.model.ChatRecord

/**
 * @author wally
 * @date 2020/09/16
 * MQTT消息回调接口
 */
interface ConversationMqttMessageReceiver {
    /**
     * 接收到离线聊天消息
     */
    fun onReceiveOfflineChatMsg(model: ChatRecord)
}