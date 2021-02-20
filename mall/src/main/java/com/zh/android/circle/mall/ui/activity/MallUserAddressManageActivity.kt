package com.zh.android.circle.mall.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.loadMainFragment
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.ui.fragment.MallUserAddressManageFragment

/**
 * @author wally
 * @date 2020/10/21
 * 用户收货地址管理
 */
@Route(path = ARouterUrl.MALL_USER_ADDRESS_MANAGE)
class MallUserAddressManageActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        loadMainFragment(
            R.id.base_container, MallUserAddressManageFragment.newInstance(
                intent.extras
            )
        )
    }
}