package com.zh.android.chat.conversation

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.module.conversation.ConversationService

/**
 * @author wally
 * @date 2020/08/26
 */
@Route(path = ARouterUrl.CONVERSATION_SERVICE, name = "会话模块服务")
class ConversationServiceImpl : ConversationService {
    override fun init(context: Context?) {
    }
}