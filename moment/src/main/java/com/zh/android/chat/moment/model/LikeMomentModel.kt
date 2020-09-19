package com.zh.android.chat.moment.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/09/19
 * 点赞、取消点赞动态后，返回的信息实体
 */
class LikeMomentModel(
    /**
     * 动态Id
     */
    val momentId: String,
    /**
     * 是否点赞
     */
    val liked: Boolean,
    /**
     * 点赞数量
     */
    val likes: Int
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}