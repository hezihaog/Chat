package com.zh.android.chat.service.module.mine.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/08/27
 * 用户信息
 */
data class User(
    /**
     * 用户Id
     */
    val id: String,
    /**
     * 昵称
     */
    val username: String,
    /**
     * 头像略缩图
     */
    val picSmall: String,
    /**
     * 头像
     */
    val picNormal: String,
    /**
     * 昵称
     */
    var nickname: String,
    /**
     * 二维码图片
     */
    val qrCode: String?,
    /**
     * 客户端Id
     */
    val clientId: String,
    /**
     * 签名
     */
    val sign: String,
    /**
     * 手机号
     */
    val phone: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}