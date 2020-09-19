package com.zh.android.chat.moment.model

import com.zh.android.chat.service.module.mine.model.User
import java.io.Serializable

/**
 * @author wally
 * @date 2020/09/19
 * 评论的回复，或者回复的回复，实体
 */
class MomentCommentReplyModel(
    /**
     * 回复的Id
     */
    val id: String,
    /**
     * 回复的评论的Id，回复评论时有值，当时回复的回复时为null
     */
    val commentId: String?,
    /**
     * 回复的回复的Id
     */
    val parentId: String?,
    /**
     * 发表回复的用户信息
     */
    val userInfo: User,
    /**
     * 被回复的人的用户Id
     */
    val replyUserId: String,
    /**
     * 回复内容
     */
    val content: String,
    /**
     * 回复的回复列表
     */
    val replyList: List<MomentCommentReplyModel>
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}