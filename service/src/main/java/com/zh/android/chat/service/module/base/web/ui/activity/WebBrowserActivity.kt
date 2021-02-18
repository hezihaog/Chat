package com.zh.android.chat.service.module.base.web.ui.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import com.zh.android.base.R
import com.zh.android.base.constant.BaseConstant
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.service.module.base.web.ui.fragment.WebBrowserFragment

/**
 * @author wally
 * @date 2020/10/07
 * 内置Web浏览器
 */
class WebBrowserActivity : BaseActivity() {
    companion object {
        /**
         * 跳转
         */
        fun start(activity: Activity, url: String) {
            activity.startActivity(Intent(activity, WebBrowserActivity::class.java).apply {
                putExtra(BaseConstant.ARGS_URL, url)
            })
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(WebBrowserFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, WebBrowserFragment.newInstance(
                    intent.extras
                )
            )
        }
    }
}