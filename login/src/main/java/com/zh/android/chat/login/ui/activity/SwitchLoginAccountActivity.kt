package com.zh.android.chat.login.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.loadMainFragment
import com.zh.android.chat.login.R
import com.zh.android.chat.login.ui.fragment.SwitchLoginAccountFragment

/**
 * @author wally
 * @date 2020/11/14
 * 切换登录账号
 */
@Route(path = ARouterUrl.LOGIN_SWITCH_LOGIN_ACCOUNT)
class SwitchLoginAccountActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        loadMainFragment(
            R.id.base_container, SwitchLoginAccountFragment.newInstance(
                intent?.extras
            )
        )
    }
}