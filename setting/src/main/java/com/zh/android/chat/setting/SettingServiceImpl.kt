package com.zh.android.chat.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.startNavigation
import com.zh.android.chat.service.module.setting.SettingService

/**
 * @author wally
 * @date 2020/08/26
 * 设置模块服务实现
 */
@Route(path = ARouterUrl.SETTING_SERVICE, name = "设置模块服务")
class SettingServiceImpl : SettingService {
    override fun init(context: Context?) {
    }

    override fun getBaseUrl(): String {
        return SettingStorage.getBaseUrl()
    }

    override fun saveEnableSwipeBack(enableSwipeBack: Boolean) {
        SettingStorage.saveEnableSwipeBack(enableSwipeBack)
        //发送切换广播
        AppBroadcastManager.sendBroadcast(
            AppConstant.Action.CHANGE_SWIPE_BACK_ENABLE,
            Intent().apply {
                putExtra(AppConstant.Key.IS_ENABLE_SWIPE_BACK, enableSwipeBack)
            })
    }

    override fun isEnableSwipeBack(): Boolean {
        return SettingStorage.isEnableSwipeBack()
    }

    override fun goSetting(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.SETTING_MAIN)
            .startNavigation(activity)
    }
}