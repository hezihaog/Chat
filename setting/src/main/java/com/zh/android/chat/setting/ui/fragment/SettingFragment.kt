package com.zh.android.chat.setting.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.widget.SwitchButton
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.module.login.LoginService
import com.zh.android.chat.service.module.setting.SettingService
import com.zh.android.chat.setting.R
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/10/16
 * 设置页面
 */
class SettingFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    @JvmField
    @Autowired(name = ARouterUrl.SETTING_SERVICE)
    var mSettingService: SettingService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vLogout: TextView by bindView(R.id.logout)
    private val vEnableSwipeBackSwitch: SwitchButton by bindView(R.id.enable_swipe_back_switch)

    companion object {
        fun newInstance(args: Bundle? = Bundle()): SettingFragment {
            val fragment = SettingFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.setting_main_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.setting_module_name)
        }
        //切换开关侧滑返回
        vEnableSwipeBackSwitch.apply {
            isChecked = mSettingService?.isEnableSwipeBack() ?: true
            setOnCheckedChangeListener { _, isChecked ->
                mSettingService?.saveEnableSwipeBack(isChecked)
            }
        }
        //退出登录
        vLogout.click {
            mLoginService?.logout(fragmentActivity)
            fragmentActivity.finish()
        }
    }
}