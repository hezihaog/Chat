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

    /**
     * 跳转到私密锁验证页面
     * @param actionCode 本次操作的唯一标识
     */
    fun goPatternLockValidate(activity: Activity, actionCode: String)

    /**
     * 跳转到私密锁设置页面
     */
    fun goPatternLockSetting(activity: Activity, requestCode: Int)
}