package com.zh.android.chat.setting.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.setting.R
import com.zh.android.chat.setting.ui.fragment.PatternLockValidateFragment

/**
 * @author wally
 * @date 2020/11/09
 * 私密锁验证页面
 */
@Route(path = ARouterUrl.SETTING_PATTERN_LOCK_VALIDATE)
class PatternLockValidateActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(PatternLockValidateFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, PatternLockValidateFragment.newInstance(
                    intent?.extras
                )
            )
        }
    }
}