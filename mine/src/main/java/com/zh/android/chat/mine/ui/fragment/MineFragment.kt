package com.zh.android.chat.mine.ui.fragment

import android.view.View
import android.widget.Button
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.mine.R
import com.zh.android.chat.service.module.login.LoginService
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/08/26
 * 我的模块首页
 */
class MineFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vLogout: Button by bindView(R.id.logout)

    override fun onInflaterViewId(): Int {
        return R.layout.mine_mine_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            setTitle(R.string.mine_mine)
        }
        vLogout.click {
            mLoginService?.logout()
        }
    }
}