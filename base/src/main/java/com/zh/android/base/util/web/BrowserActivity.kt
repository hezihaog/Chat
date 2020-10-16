package com.zh.android.base.util.web

import android.app.Activity
import android.content.Intent
import android.view.View
import com.zh.android.base.R
import com.zh.android.base.core.BaseActivity

/**
 * @author wally
 * @date 2020/10/07
 * 内置Web浏览器
 */
class BrowserActivity : BaseActivity() {
    companion object {
        const val ARGS_URL = "args_url"

        /**
         * 跳转
         */
        fun start(activity: Activity, url: String) {
            activity.startActivity(Intent(activity, BrowserActivity::class.java).apply {
                putExtra(ARGS_URL, url)
            })
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(BrowserFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, BrowserFragment.newInstance(
                    intent.extras
                )
            )
        }
    }
}