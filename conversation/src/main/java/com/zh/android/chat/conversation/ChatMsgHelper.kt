package com.zh.android.chat.conversation

import android.content.Context
import com.zh.android.chat.service.module.conversation.enums.ChatMsgType
import com.zh.android.chat.service.module.conversation.model.ChatRecord
import com.zh.android.chat.service.module.conversation.model.Conversation

/**
 * @author wally
 * @date 2020/09/25
 */
object ChatMsgHelper {
    fun getChatText(context: Context, model: ChatRecord): String {
        return getChatTextByType(context, model.type, model.text?.content ?: "")
    }

    fun getChatText(context: Context, model: Conversation): String {
        return getChatTextByType(context, model.type, model.text?.content ?: "")
    }

    /**
     * 根据类型，获取聊天文字
     */
    private fun getChatTextByType(context: Context, type: Int, textContent: String): String {
        return when (type) {
            ChatMsgType.TEXT.code -> {
                textContent
            }
            ChatMsgType.IMAGE.code -> {
                context.resources.getString(R.string.conversation_image_omit)
            }
            ChatMsgType.VOICE.code -> {
                context.resources.getString(R.string.conversation_voice_omit)
            }
            else -> {
                ""
            }
        }
    }
}