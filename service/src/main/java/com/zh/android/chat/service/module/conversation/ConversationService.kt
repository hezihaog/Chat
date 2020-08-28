package com.zh.android.chat.service.module.conversation

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author wally
 * @date 2020/08/26
 */
interface ConversationService : IProvider {
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
}