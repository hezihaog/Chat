package com.zh.android.chat

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.service.module.home.HomeService

/**
 * @author wally
 * @date 2020/08/26
 */
class LauncherActivity : BaseActivity() {
    @JvmField
    @Autowired(name = ARouterUrl.HOME_SERVICE)
    var mSettingService: HomeService? = null

    override fun onInflaterViewId(): Int {
        return -1
    }

    override fun onBindView(view: View?) {
        mSettingService?.goHome(this)
        finish()
    }
}