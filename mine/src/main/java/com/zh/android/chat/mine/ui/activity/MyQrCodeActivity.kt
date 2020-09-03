package com.zh.android.chat.mine.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.mine.R
import com.zh.android.chat.mine.ui.fragment.MyQrCodeFragment

/**
 * @author wally
 * @date 2020/09/03
 * 我的二维码
 */
@Route(path = ARouterUrl.MINE_MY_QR_CODE)
class MyQrCodeActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(MyQrCodeFragment::class.java) == null) {
            loadRootFragment(R.id.base_container, MyQrCodeFragment.newInstance())
        }
    }
}