package com.zh.android.chat.setting.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.loadMainFragment
import com.zh.android.chat.setting.R
import com.zh.android.chat.setting.ui.fragment.SettingFragment

/**
 * @author wally
 * @date 2020/10/16
 */
@Route(path = ARouterUrl.SETTING_MAIN)
class SettingActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        loadMainFragment(
            R.id.base_container, SettingFragment.newInstance(
                intent.extras
            )
        )
    }
}