package com.zh.android.chat.conversation.ws.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/09/25
 * 已读消息
 */
class ReadMsgModel(
    /**
     * 聊天消息记录Id
     */
    val recordId: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}