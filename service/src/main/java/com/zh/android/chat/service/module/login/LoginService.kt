package com.zh.android.chat.service.module.login

import android.app.Activity
import com.alibaba.android.arouter.facade.callback.NavigationCallback
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
     * 获取Token
     */
    fun getToken(): String

    /**
     * 跳转到登录
     * @param isClearOther 是否清除其他页面
     * @param callback 跳转回调
     */
    fun goLogin(
        activity: Activity,
        isClearOther: Boolean,
        callback: NavigationCallback? = null
    )

    /**
     * 退出登录
     */
    fun logout(activity: Activity)
}