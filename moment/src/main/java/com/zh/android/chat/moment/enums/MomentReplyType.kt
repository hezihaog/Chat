package com.zh.android.chat.moment.enums

/**
 * @author wally
 * @date 2020/09/19
 * 动态回复类型
 */
enum class MomentReplyType(
    val code: Int
) {
    /**
     * 评论的回复
     */
    COMMENT_REPLY(1),

    /**
     * 回复的回复
     */
    REPLY_REPLY(2)
}