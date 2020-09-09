package com.zh.android.chat.login.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.login.R
import com.zh.android.chat.login.ui.fragment.LoginByPhoneFragment

/**
 * @author wally
 * @date 2020/09/09
 * 手机号登录
 */
@Route(path = ARouterUrl.LOGIN_BY_PHONE)
class LoginByPhoneActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(LoginByPhoneFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, LoginByPhoneFragment.newInstance(
                    intent.extras
                )
            )
        }
    }
}