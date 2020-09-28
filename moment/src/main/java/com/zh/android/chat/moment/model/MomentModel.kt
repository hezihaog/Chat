package com.zh.android.chat.moment.model

import com.zh.android.chat.service.module.mine.model.User
import java.io.Serializable

/**
 * @author wally
 * @date 2020/09/19
 * 动态列表实体
 */
class MomentModel(
    /**
     * 动态Id
     */
    val id: String,
    /**
     * 用户信息
     */
    val userInfo: User,
    /**
     * 动态内容
     */
    val content: String,
    /**
     * 图片列表
     */
    val pictures: List<String>,
    /**
     * 视频列表
     */
    val videos: List<String>,
    /**
     * 点赞数量
     */
    var likes: Int,
    /**
     * 评论数量
     */
    val comments: Int,
    /**
     * 是否点赞了
     */
    var liked: Boolean,
    /**
     * 是否是我的动态
     */
    val me: Boolean,
    /**
     * 创建时间
     */
    val createTime: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}