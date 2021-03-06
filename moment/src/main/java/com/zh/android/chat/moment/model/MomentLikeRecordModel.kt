package com.zh.android.chat.moment.model

import com.zh.android.chat.service.module.mine.model.User
import java.io.Serializable

/**
 * @author wally
 * @date 2020/09/19
 */
data class MomentLikeRecordModel(
    /**
     * 点赞记录Id
     */
    val id: String,
    /**
     * 动态Id
     */
    val momentId: String,
    /**
     * 点赞人的用户信息
     */
    val userInfo: User,
    /**
     * 创建时间
     */
    val createTime: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}