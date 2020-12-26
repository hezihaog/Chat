package com.zh.android.circle.gankio

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.constant.BaseConstant
import com.zh.android.chat.service.ext.startNavigation
import com.zh.android.chat.service.module.gankio.GankioService

/**
 * @author wally
 * @date 2020/12/26
 */
@Route(path = ARouterUrl.GANKIO_SERVICE, name = "代办事项模块服务")
class GankioServiceImpl : GankioService {
    override fun init(context: Context?) {
    }

    override fun goGankioHome(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.GANKIO_HOME)
            .withBoolean(BaseConstant.IS_SHOW_TOP_BAR, false)
            .withString(BaseConstant.ARGS_URL, "file:///android_asset/gankio/index.html")
            .startNavigation(activity)
    }
}