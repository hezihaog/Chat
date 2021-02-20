package com.zh.android.circle.mall.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.loadMainFragment
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.ui.fragment.MallMainFragment

/**
 * @author wally
 * @date 2020/10/16
 * 商城首页
 */
@Route(path = ARouterUrl.MALL_MAIN)
class MallMainActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        loadMainFragment(
            R.id.base_container, MallMainFragment.newInstance(
                intent.extras
            )
        )
    }
}