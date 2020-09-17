package com.zh.android.chat.login

import com.blankj.utilcode.util.SPUtils


/**
 * <b>Package:</b> com.linghit.login <br>
 * <b>Create Date:</b> 2019-07-01  09:08 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 登录模块-键值对本地存储
 * <br>
 */
object LoginStorage {
    /**
     * 用户Id
     */
    private const val KEY_USER_ID = "login_key_user_id"

    /**
     * 用户名
     */
    private const val KEY_USERNAME = "login_key_username"

    /**
     * Token令牌
     */
    private const val KEY_TOKEN = "login_key_token"

    @JvmStatic
    fun saveUserId(userId: String) {
        SPUtils.getInstance().put(KEY_USER_ID, userId)
    }

    @JvmStatic
    fun getUserId(): String {
        return SPUtils.getInstance().getString(KEY_USER_ID)
    }

    @JvmStatic
    fun saveUsername(username: String) {
        SPUtils.getInstance().put(KEY_USERNAME, username)
    }

    @JvmStatic
    fun getUsername(): String {
        return SPUtils.getInstance().getString(KEY_USERNAME)
    }

    @JvmStatic
    fun saveToken(token: String) {
        SPUtils.getInstance().put(KEY_TOKEN, token)
    }

    @JvmStatic
    fun getToken(): String {
        return SPUtils.getInstance().getString(KEY_TOKEN)
    }

    /**
     * 判断是否登录
     */
    @JvmStatic
    fun isLogin(): Boolean {
        return getToken().isNotBlank()
    }

    /**
     * 清除登录信息
     */
    @JvmStatic
    fun clean() {
        SPUtils.getInstance().run {
            remove(KEY_USER_ID)
            remove(KEY_USERNAME)
            remove(KEY_TOKEN)
        }
    }
}