package com.zh.android.chat.notice.service

import com.zh.android.chat.service.module.notice.model.NoticeModel

/**
 * @author wally
 * @date 2020/10/13
 */
interface NoticeMqttMessageReceiver {
    /**
     * 接收到通知消息
     */
    fun onReceiveNoticeMsg(model: NoticeModel)
}