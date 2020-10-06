package com.zh.android.chat.notice.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.notice.R
import com.zh.android.chat.notice.ui.fragment.NoticeFragment

/**
 * @author wally
 * @date 2020/10/06
 * 通知页面
 */
@Route(path = ARouterUrl.NOTICE_MAIN)
class NoticeActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(NoticeFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, NoticeFragment.newInstance(
                    intent.extras
                )
            )
        }
    }
}