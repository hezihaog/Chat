package com.zh.android.chat.home

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.core.BaseModuleService
import com.zh.android.chat.service.ext.startNavigation
import com.zh.android.chat.service.module.home.HomeService

/**
 * @author wally
 * @date 2020/08/26
 */
@Route(path = ARouterUrl.HOME_SERVICE, name = "首页模块服务")
class HomeServiceImpl : BaseModuleService(), HomeService {
    override fun init(context: Context?) {
    }

    override fun goHome() {
        ARouter.getInstance().build(ARouterUrl.HOME_HOME)
            .navigation()
    }

    override fun goHome(activity: Activity, callback: NavigationCallback?) {
        ARouter.getInstance()
            .build(ARouterUrl.HOME_HOME)
            .startNavigation(activity, callback = callback)
    }
}