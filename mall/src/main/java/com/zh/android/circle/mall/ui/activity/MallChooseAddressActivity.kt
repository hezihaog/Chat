package com.zh.android.circle.mall.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.ui.fragment.MallChooseAddressFragment

/**
 * @author wally
 * @date 2020/10/24
 * 选择用户收货地址
 */
@Route(path = ARouterUrl.MALL_USER_CHOOSE_ADDRESS)
class MallChooseAddressActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(MallChooseAddressFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, MallChooseAddressFragment.newInstance(
                    intent.extras
                )
            )
        }
    }
}