package com.zh.android.circle.mall.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.ui.fragment.MallShoppingCarFragment

/**
 * @author wally
 * @date 2020/10/20
 * 购物车
 */
@Route(path = ARouterUrl.MALL_SHOPPING_CAR)
class MallShoppingCarActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(MallShoppingCarFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, MallShoppingCarFragment.newInstance(
                    intent.extras
                )
            )
        }
    }
}