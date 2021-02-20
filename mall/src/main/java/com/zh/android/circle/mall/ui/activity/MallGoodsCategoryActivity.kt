package com.zh.android.circle.mall.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.loadMainFragment
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.ui.fragment.MallGoodsCategoryFragment

/**
 * @author wally
 * @date 2020/10/19
 * 商品分类
 */
@Route(path = ARouterUrl.MALL_GOODS_CATEGORY)
class MallGoodsCategoryActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        loadMainFragment(
            R.id.base_container, MallGoodsCategoryFragment.newInstance(
                intent.extras
            )
        )
    }
}