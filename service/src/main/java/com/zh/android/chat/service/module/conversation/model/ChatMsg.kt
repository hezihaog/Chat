package com.zh.android.chat.service.module.conversation.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/09/25
 * 发送的聊天消息实体
 */
data class ChatMsg(
    /**
     * 聊天消息类型，详情见ChatMsgType
     */
    val chatMsgType: Int,
    /**
     * 发消息的用户的Id
     */
    val fromUserId: String,
    /**
     * 接收消息的用户的Id
     */
    val toUserId: String,
    /**
     * 文字内容
     */
    val textContent: String? = null,
    /**
     * 图片地址
     */
    val image: String? = null,
    /**
     * 音频地址
     */
    val mediaSrc: String? = null,
    /**
     * 音频市场
     */
    val mediaTime: Int? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}