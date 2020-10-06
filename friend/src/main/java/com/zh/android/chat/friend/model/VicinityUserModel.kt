package com.zh.android.chat.friend.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/06
 * 附近的人实体
 */
data class VicinityUserModel(
    /**
     * 经度
     */
    val longitude: Double,
    /**
     * 纬度
     */
    val latitude: Double,
    /**
     * 用户Id
     */
    val userId: String,
    /**
     * 用户名
     */
    val username: String,
    /**
     * 头像
     */
    val avatar: String,
    /**
     * 昵称
     */
    val nickname: String,
    /**
     * 签名
     */
    val sign: String?,
    /**
     * 是否发送了好友申请
     */
    var isSendRequest:Boolean,
    /**
     * 好友申请状态
     */
    var status: Int
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}