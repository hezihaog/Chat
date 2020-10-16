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
     * 跳转到设置首页
     */
    fun goSetting(activity: Activity)
}