package com.zh.android.circle.mall.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.ui.fragment.UserAddressEditFragment

/**
 * @author wally
 * @date 2020/10/21
 * 用户资料编辑
 */
@Route(path = ARouterUrl.MALL_USER_ADDRESS_EDIT)
class UserAddressEditActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(UserAddressEditFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, UserAddressEditFragment.newInstance(
                    intent.extras
                )
            )
        }
    }
}