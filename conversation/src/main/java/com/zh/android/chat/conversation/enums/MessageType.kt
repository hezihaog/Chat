package com.zh.android.chat.conversation.enums

/**
 * @author wally
 * @date 2020/08/28
 * 消息类型枚举
 */
enum class MessageType(val code: Int) {
    /**
     * 连接消息
     */
    CONNECTION(0),

    /**
     * 发送消息
     */
    SEND(1),

    /**
     * 已读消息
     */
    READ_MSG(2),

    /**
     * 心跳消息
     */
    HEAR_BEAT(3)
}