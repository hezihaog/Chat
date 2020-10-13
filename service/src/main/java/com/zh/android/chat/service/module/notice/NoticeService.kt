package com.zh.android.chat.service.module.notice

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider
import com.zh.android.chat.service.module.notice.model.NoticeModel

/**
 * @author wally
 * @date 2020/10/06
 * 通知模块接口
 */
interface NoticeService : IProvider {
    /**
     * 跳转到通知页面
     */
    fun goNotice(activity: Activity)

    /**
     * 开启会话模块的Mqtt服务
     */
    fun startMqttService()

    /**
     * 发送通知消息到通知栏通知
     * @param notice 通知
     */
    fun sendNoticeMessageNotification(notice: NoticeModel)
}