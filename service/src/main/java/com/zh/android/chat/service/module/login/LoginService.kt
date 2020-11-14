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
     * 保存私密锁字符串
     */
    fun savePatternLockStr(encryptStr: String)

    /**
     * 获取私密锁字符串
     */
    fun getPatternLockStr(): String

    /**
     * 保存是否开启私密锁
     */
    fun saveIsOpenPatternLock(isOpen: Boolean)

    /**
     * 是否开启私密锁
     */
    fun isOpenPatternLock(): Boolean

    /**
     * 跳转到登录
     * @param isClearOther 是否关闭其他页面
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