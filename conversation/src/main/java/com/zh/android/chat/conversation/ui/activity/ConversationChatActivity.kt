package com.zh.android.chat.conversation.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.loadMainFragment
import com.zh.android.chat.conversation.R
import com.zh.android.chat.conversation.ui.fragment.ConversationChatFragment

/**
 * @author wally
 * @date 2020/08/28
 * 聊天会话页面
 */
@Route(path = ARouterUrl.CONVERSATION_CHAT)
class ConversationChatActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        loadMainFragment(R.id.base_container, ConversationChatFragment.newInstance(
            Bundle(intent.extras)
        ))
    }
}