package com.zh.android.chat.conversation.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/08/28
 * 聊天消息实体
 */
data class Message(
    /**
     * 消息类型
     */
    val type: Int,
    /**
     * 消息记录信息
     */
    val chatRecord: ChatRecord? = null,
    /**
     * 拓展消息字段
     */
    val ext: Any? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}