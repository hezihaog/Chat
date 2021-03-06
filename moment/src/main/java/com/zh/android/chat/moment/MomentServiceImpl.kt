package com.zh.android.chat.moment

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.core.BaseModuleService
import com.zh.android.chat.service.ext.startNavigation
import com.zh.android.chat.service.module.moment.MomentService
import com.zh.android.chat.service.module.moment.enums.MomentPublishType

/**
 * @author wally
 * @date 2020/09/19
 */
@Route(path = ARouterUrl.MOMENT_SERVICE, name = "动态模块服务")
class MomentServiceImpl : MomentService, BaseModuleService() {
    override fun init(context: Context?) {
    }

    override fun goMomentList(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.MOMENT_LIST)
            .startNavigation(activity)
    }

    override fun goMyMomentList(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.MOMENT_MY_LIST)
            .withBoolean(AppConstant.Key.IS_MY_MOMENT, true)
            .startNavigation(activity)
    }

    override fun goMomentVideoList(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.MOMENT_VIDEO_LIST)
            .startNavigation(activity)
    }

    override fun goMomentSearch(activity: Activity, isMyMoment: Boolean) {
        ARouter.getInstance()
            .build(ARouterUrl.MOMENT_SEARCH)
            .withBoolean(AppConstant.Key.IS_MY_MOMENT, isMyMoment)
            .startNavigation(activity)
    }

    override fun goMomentDetail(activity: Activity, momentId: String) {
        ARouter.getInstance()
            .build(ARouterUrl.MOMENT_DETAIL)
            .withString(AppConstant.Key.MOMENT_ID, momentId)
            .startNavigation(activity)
    }

    override fun goMomentPublish(activity: Activity, type: MomentPublishType) {
        ARouter.getInstance()
            .build(ARouterUrl.MOMENT_PUBLISH)
            .withSerializable(AppConstant.Key.MOMENT_PUBLISH_TYPE, type)
            .startNavigation(activity)
    }

    override fun goMomentCommentDetail(
        activity: Activity,
        momentId: String,
        momentCommentId: String
    ) {
        ARouter.getInstance()
            .build(ARouterUrl.MOMENT_COMMENT_DETAIL)
            .withString(AppConstant.Key.MOMENT_ID, momentId)
            .withString(AppConstant.Key.MOMENT_COMMENT_ID, momentCommentId)
            .startNavigation(activity)
    }
}