package com.zh.android.chat.login.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/08/26
 * 登录返回信息
 */
data class LoginModel(
    /**
     * 用户Id
     */
    val id: String,
    /**
     * 用户名
     */
    val username: String,
    /**
     * 昵称
     */
    val nickname: String,
    /**
     * 头像
     */
    val avatar: String,
    /**
     * 令牌
     */
    val token: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}