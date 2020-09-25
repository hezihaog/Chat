package com.zh.android.chat.service.module.conversation.model

import com.zh.android.chat.service.module.mine.model.User
import java.io.Serializable

/**
 * @author wally
 * @date 2020/08/30
 * 会话信息
 */
data class Conversation(
    /**
     * 记录Id
     */
    val id: String,
    /**
     * 消息发送人信息
     */
    val fromUser: User,
    /**
     * 消息接收人
     */
    val toUser: User,
    /**
     * 类型
     */
    val type:Int,
    /**
     * 发送的消息
     */
    val text: ChatRecord.TextVO,
    /**
     * 是否是我发送的
     */
    var isMe: Boolean,
    /**
     * 是否已读
     */
    val hasRead: Int,
    /**
     * 创建时间
     */
    val createTime: String,
    /**
     * 是否已删除
     */
    val hasDelete: Int
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}