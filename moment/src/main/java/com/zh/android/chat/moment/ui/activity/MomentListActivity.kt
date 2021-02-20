package com.zh.android.chat.moment.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.loadMainFragment
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.ui.fragment.MomentListFragment

/**
 * @author wally
 * @date 2020/09/19
 * 动态列表
 */
@Route(path = ARouterUrl.MOMENT_LIST)
class MomentListActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        loadMainFragment(
            R.id.base_container, MomentListFragment.newInstance(
                intent.extras
            )
        )
    }
}