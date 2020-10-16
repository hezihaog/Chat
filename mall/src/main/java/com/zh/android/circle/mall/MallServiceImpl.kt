package com.zh.android.circle.mall

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.util.web.BrowserActivity
import com.zh.android.chat.service.module.mall.MallService

/**
 * @author wally
 * @date 2020/10/16
 */
@Route(path = ARouterUrl.MALL_SERVICE, name = "商城模块服务")
class MallServiceImpl : MallService {
    override fun init(context: Context?) {
    }

    override fun goMall(activity: Activity) {
        BrowserActivity.start(activity, "http://47.99.134.126:5000/#/home")
    }
}