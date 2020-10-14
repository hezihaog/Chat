package com.zh.android.chat.moment.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.ui.fragment.MomentListFragment
import com.zh.android.chat.moment.ui.fragment.MomentSearchFragment

/**
 * @author wally
 * @date 2020/10/14
 * 动态搜索
 */
@Route(path = ARouterUrl.MOMENT_SEARCH)
class MomentSearchActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(MomentListFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, MomentSearchFragment.newInstance(
                    intent.extras
                )
            )
        }
    }
}