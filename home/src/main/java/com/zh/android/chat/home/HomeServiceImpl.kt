package com.zh.android.chat.home

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.module.home.HomeService

/**
 * @author wally
 * @date 2020/08/26
 */
@Route(path = ARouterUrl.HOME_SERVICE, name = "首页模块服务")
class HomeServiceImpl : HomeService {
    override fun init(context: Context?) {
    }

    override fun goHome(activity: Activity) {
        HomeUIHelper.goHome(activity)
    }
}