package com.zh.android.chat.service.module.base.web.ui.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import com.zh.android.base.R
import com.zh.android.base.constant.BaseConstant
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.loadMainFragment
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

    override fun swipeBackOnlyEdge(): Boolean {
        //因为WebView中的轮播图左右滑动会被侧滑布局影响导致被先拦截，所以只允许边缘滑动来规避这个问题
        return true
    }

    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        loadMainFragment(R.id.base_container, WebBrowserFragment.newInstance(
            intent.extras
        ))
    }
}