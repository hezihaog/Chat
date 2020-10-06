package com.zh.android.chat.notice

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.ext.startNavigation
import com.zh.android.chat.service.module.notice.NoticeService

/**
 * @author wally
 * @date 2020/10/06
 * 通知服务实现
 */
@Route(path = ARouterUrl.NOTICE_SERVICE, name = "通知服务")
class NoticeServiceImpl : NoticeService {
    override fun init(context: Context?) {
    }

    override fun goNotice(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.NOTICE_MAIN)
            .startNavigation(activity)
    }
}