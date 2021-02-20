package com.zh.android.chat.moment.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.loadMainFragment
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.ui.fragment.MomentVideoListFragment

/**
 * @author wally
 * @date 2020/09/28
 * 视频动态
 */
@Route(path = ARouterUrl.MOMENT_VIDEO_LIST)
class MomentVideoListActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        loadMainFragment(
            R.id.base_container, MomentVideoListFragment.newInstance(
                intent.extras
            )
        )
    }
}