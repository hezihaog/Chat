package com.zh.android.chat.notice

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.util.NotificationUtil
import com.zh.android.base.util.monitor.AppMonitor
import com.zh.android.chat.notice.service.NoticeMqttService
import com.zh.android.chat.notice.ui.activity.NoticeActivity
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.core.BaseModuleService
import com.zh.android.chat.service.ext.startNavigation
import com.zh.android.chat.service.module.notice.NoticeService
import com.zh.android.chat.service.module.notice.model.NoticeModel

/**
 * @author wally
 * @date 2020/10/06
 * 通知服务实现
 */
@Route(path = ARouterUrl.NOTICE_SERVICE, name = "通知服务")
class NoticeServiceImpl : BaseModuleService(), NoticeService {
    private lateinit var mContext: Context

    override fun init(context: Context?) {
        mContext = context!!
    }

    override fun goNotice(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.NOTICE_MAIN)
            .startNavigation(activity)
    }

    override fun startMqttService() {
        AppMonitor.get().run {
            if (isAppForeground) {
                mContext.startService(Intent(mContext, NoticeMqttService::class.java))
            } else {
                register(object : AppMonitor.CallbackAdapter() {
                    override fun onAppForeground() {
                        super.onAppForeground()
                        mContext.startService(Intent(mContext, NoticeMqttService::class.java))
                    }
                })
            }
        }
    }

    override fun sendNoticeMessageNotification(notice: NoticeModel) {
        notice.let {
            NotificationUtil.create(
                mContext,
                10010,
                mContext.resources.getString(R.string.notice_message_notification_channel_id),
                mContext.resources.getString(R.string.notice_message_notification_channel_name),
                //跳转到通知页面
                Intent(mContext, NoticeActivity::class.java).apply {
                    putExtra(AppConstant.Key.NOTICE_ID, it.id)
                    putExtra(AppConstant.Key.NOTICE_DETAIL, it.detail)
                },
                R.drawable.base_notification_icon,
                notice.title,
                //提示文字
                notice.content,
                false,
                true,
                true,
                false
            )
        }
    }
}