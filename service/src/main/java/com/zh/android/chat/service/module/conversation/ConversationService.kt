package com.zh.android.chat.service.module.conversation

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
}