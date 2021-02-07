package com.zh.android.chat.service.module.home

import android.app.Activity
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.zh.android.chat.service.core.IBaseModuleService

/**
 * @author wally
 * @date 2020/08/26
 */
interface HomeService : IBaseModuleService {
    /**
     * 跳转到主页
     */
    fun goHome()

    /**
     * 跳转到主页，支持设置回调监听
     * @param callback 跳转回调
     */
    fun goHome(activity: Activity, callback: NavigationCallback? = null)
}