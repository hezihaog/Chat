package com.zh.android.chat.moment.model

import com.zh.android.chat.service.module.mine.model.User
import java.io.Serializable

/**
 * @author wally
 * @date 2020/09/19
 * 动态评论实体
 */
class MomentCommentModel(
    /**
     * 评论Id
     */
    val id: String,
    /**
     * 动态Id
     */
    val momentId: String,
    /**
     * 发表评论的用户信息
     */
    val userInfo: User,
    /**
     * 评论内容
     */
    val content: String,
    /**
     * 是否是我发的动态评论
     */
    val me: Boolean,
    /**
     * 创建时间
     */
    val createTime: String,
    /**
     * 评论的回复列表
     */
    val replyList: MutableList<MomentCommentReplyModel>
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}