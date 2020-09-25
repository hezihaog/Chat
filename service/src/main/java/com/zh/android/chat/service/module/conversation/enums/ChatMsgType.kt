package com.zh.android.chat.service.module.conversation.enums

/**
 * @author wally
 * @date 2020/09/25
 * 聊天消息类型
 */
enum class ChatMsgType(val code: Int) {
    /**
     * 文字消息
     */
    TEXT(1),

    /**
     * 图片消息
     */
    IMAGE(2),

    /**
     * 语音消息
     */
    VOICE(3);

    companion object {
        /**
         * 判断是否合法
         */
        fun isValid(code: Int): Boolean {
            for (value in values()) {
                if (code == value.code) {
                    return true
                }
            }
            return false
        }
    }
}