package com.zh.android.chat.service.module.base.web.ui.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.service.R
import com.zh.android.chat.service.module.base.web.ui.fragment.WebCollectListFragment

/**
 * @author wally
 * @date 2021/02/18
 * Web收藏列表
 */
class WebCollectListActivity : BaseActivity() {
    companion object {
        /**
         * 跳转
         */
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, WebCollectListActivity::class.java))
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(WebCollectListFragment::class.java) == null) {
            loadRootFragment(R.id.base_container, WebCollectListFragment.newInstance())
        }
    }
}