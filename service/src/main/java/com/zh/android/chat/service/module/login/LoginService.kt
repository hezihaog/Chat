package com.zh.android.chat.service.module.login

import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author wally
 * @date 2020/08/26
 */
interface LoginService : IProvider {
    /**
     * 是否登录了
     */
    fun isLogin(): Boolean

    /**
     * 获取用户Id
     */
    fun getUserId(): String

    /**
     * 跳转到登录
     */
    fun goLogin()

    /**
     * 退出登录
     */
    fun logout()
}