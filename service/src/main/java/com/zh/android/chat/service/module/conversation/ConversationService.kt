package com.zh.android.chat.service.module.conversation

import android.app.Activity
import com.zh.android.chat.service.core.IBaseModuleService
import com.zh.android.chat.service.module.conversation.model.ChatRecord

/**
 * @author wally
 * @date 2020/08/26
 */
interface ConversationService : IBaseModuleService {
    /**
     * 获取会话首页
     */
    fun getConversationMainFragment(): String

    /**
     * 跳转到聊天会话
     * @param friendUserId 要聊天的好友的用户Id
     * @param friendNickName 好友的昵称
     */
    fun goConversationChat(
        activity: Activity,
        friendUserId: String,
        friendNickName: String
    )

    /**
     * 开启会话模块的Mqtt服务
     */
    fun startMqttService()

    /**
     * 发送离线聊天消息通知栏通知
     * @param chatRecord 聊天消息记录
     */
    fun sendOfflineChatMessageNotification(chatRecord: ChatRecord)
}