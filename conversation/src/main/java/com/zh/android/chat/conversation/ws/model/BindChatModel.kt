package com.zh.android.chat.conversation.ws.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/09/25
 * 绑定聊天室
 */
class BindChatModel(
    /**
     * 用户Id
     */
    val userId: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}