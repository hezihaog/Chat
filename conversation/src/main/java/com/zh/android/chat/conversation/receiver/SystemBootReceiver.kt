package com.zh.android.chat.conversation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.apkfuns.logutils.LogUtils
import com.zh.android.chat.service.ext.getConversationService
import com.zh.android.chat.service.ext.getNoticeService

/**
 * <b>Package:</b> com.tongwei.smarttoilet.base.receiver <br>
 * <b>Create Date:</b> 2019-10-10  22:03 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 系统启动广播接收器 <br>
 */
class SystemBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //启动Mqtt服务
        val conversationService = getConversationService()
        if (conversationService != null) {
            conversationService.startMqttService()
            LogUtils.d("设备系统启动 => 启动 Conversation Mqtt服务成功")
        } else {
            LogUtils.d("设备系统启动 => 启动 Conversation Mqtt服务失败")
        }
        val noticeService = getNoticeService()
        if (noticeService != null) {
            noticeService.startMqttService()
            LogUtils.d("设备系统启动 => 启动 Notice Mqtt服务成功")
        } else {
            LogUtils.d("设备系统启动 => 启动 Notice Mqtt服务失败")
        }
    }
}