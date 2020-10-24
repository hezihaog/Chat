package com.zh.android.circle.mall.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.ui.fragment.MallCreateOrderFragment

/**
 * @author wally
 * @date 2020/10/24
 * 创建订单页面
 */
@Route(path = ARouterUrl.MALL_CREATE_ORDER)
class MallCreateOrderActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(MallCreateOrderFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, MallCreateOrderFragment.newInstance(
                    intent.extras
                )
            )
        }
    }
}