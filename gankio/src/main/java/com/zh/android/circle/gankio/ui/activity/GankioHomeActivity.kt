package com.zh.android.circle.gankio.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.R
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.loadMainFragment
import com.zh.android.chat.service.module.base.web.ui.fragment.WebBrowserFragment

/**
 * @author wally
 * @date 2020/12/26
 */
@Route(path = ARouterUrl.GANKIO_HOME)
class GankioHomeActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        loadMainFragment(
            R.id.base_container, WebBrowserFragment.newInstance(
                intent.extras
            )
        )
    }
}