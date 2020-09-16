package com.zh.android.chat.conversation

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.util.monitor.AppMonitor
import com.zh.android.chat.conversation.service.ConversationMqttService
import com.zh.android.chat.conversation.ui.fragment.ConversationMainFragment
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.startNavigation
import com.zh.android.chat.service.module.conversation.ConversationService

/**
 * @author wally
 * @date 2020/08/26
 */
@Route(path = ARouterUrl.CONVERSATION_SERVICE, name = "会话模块服务")
class ConversationServiceImpl : ConversationService {
    private lateinit var mContext: Context

    override fun init(context: Context?) {
        mContext = context!!
    }

    override fun getConversationMainFragment(): String {
        return ConversationMainFragment::class.java.name
    }

    override fun goConversationChat(
        activity: Activity,
        friendUserId: String,
        friendNickName: String
    ) {
        ARouter.getInstance()
            .build(ARouterUrl.CONVERSATION_CHAT)
            .withString(AppConstant.Key.USER_ID, friendUserId)
            .withString(AppConstant.Key.NICK_NAME, friendNickName)
            .startNavigation(activity)
    }

    override fun startMqttService() {
        AppMonitor.get().run {
            if (isAppForeground) {
                mContext.startService(Intent(mContext, ConversationMqttService::class.java))
            } else {
                register(object : AppMonitor.CallbackAdapter() {
                    override fun onAppForeground() {
                        super.onAppForeground()
                        mContext.startService(Intent(mContext, ConversationMqttService::class.java))
                    }
                })
            }
        }
    }
}