package com.zh.android.chat.login.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.loadMainFragment
import com.zh.android.chat.login.R
import com.zh.android.chat.login.ui.fragment.LoginFragment

/**
 * @author wally
 * @date 2020/08/26
 * 登录页面
 */
@Route(path = ARouterUrl.LOGIN_LOGIN)
class LoginActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        loadMainFragment(
            R.id.base_container, LoginFragment.newInstance(
                intent?.extras
            )
        )
    }
}