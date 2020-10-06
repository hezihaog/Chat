package com.zh.android.chat.friend.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.friend.R
import com.zh.android.chat.friend.ui.fragment.VicinityUserFragment

/**
 * @author wally
 * @date 2020/10/06
 * 附近的人
 */
@Route(path = ARouterUrl.FRIEND_VICINITY_USER)
class VicinityUserActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(VicinityUserFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, VicinityUserFragment.newInstance(
                    intent.extras
                )
            )
        }
    }
}