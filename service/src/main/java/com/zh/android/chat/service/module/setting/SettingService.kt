package com.zh.android.chat.service.module.setting

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author wally
 * @date 2020/08/26
 */
interface SettingService : IProvider {
    /**
     * 获取配置的BaseUrl
     */
    fun getBaseUrl(): String

    /**
     * 设置是否开启侧滑关闭功能
     */
    fun saveEnableSwipeBack(enableSwipeBack: Boolean)

    /**
     * 获取是否开启侧滑关闭功能
     */
    fun isEnableSwipeBack(): Boolean

    /**
     * 跳转到设置首页
     */
    fun goSetting(activity: Activity)
}