package com.zh.android.chat.moment

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.startNavigation
import com.zh.android.chat.service.module.moment.MomentService

/**
 * @author wally
 * @date 2020/09/19
 */
@Route(path = ARouterUrl.MOMENT_SERVICE, name = "动态模块服务")
class MomentServiceImpl : MomentService {
    override fun init(context: Context?) {
    }

    override fun goMomentList(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.MOMENT_LIST)
            .startNavigation(activity)
    }

    override fun goMomentDetail(activity: Activity, momentId: String) {
        ARouter.getInstance()
            .build(ARouterUrl.MOMENT_DETAIL)
            .withString(AppConstant.Key.MOMENT_ID, momentId)
            .startNavigation(activity)
    }
}