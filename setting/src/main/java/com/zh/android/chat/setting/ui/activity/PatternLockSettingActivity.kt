package com.zh.android.chat.setting.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.setting.R
import com.zh.android.chat.setting.ui.fragment.PatternLockSettingFragment

/**
 * @author wally
 * @date 2020/11/10
 * 私密锁设置页面
 */
@Route(path = ARouterUrl.SETTING_PATTERN_LOCK_SETTING)
class PatternLockSettingActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(PatternLockSettingFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, PatternLockSettingFragment.newInstance(
                    intent?.extras
                )
            )
        }
    }
}