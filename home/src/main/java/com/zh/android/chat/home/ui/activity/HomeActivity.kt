package com.zh.android.chat.home.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.home.R
import com.zh.android.chat.home.ui.fragment.HomeMainFragment
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.module.conversation.ConversationService

/**
 * @author wally
 * @date 2020/08/26
 */
@Route(path = ARouterUrl.HOME_HOME, extras = AppConstant.Flag.IS_NEED_LOGIN)
class HomeActivity : BaseActivity() {
    @JvmField
    @Autowired(name = ARouterUrl.CONVERSATION_SERVICE)
    var mConversationService: ConversationService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //开启会话模块的推送服务
        mConversationService?.startMqttService()
    }

    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(HomeMainFragment::class.java) == null) {
            loadRootFragment(R.id.base_container, HomeMainFragment.newInstance())
        }
    }
}