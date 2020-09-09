package com.zh.android.chat.discovery.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.discovery.R
import com.zh.android.chat.discovery.ui.fragment.QrCodeScanFragment

/**
 * @author wally
 * @date 2020/09/09
 * 二维码扫描
 */
@Route(path = ARouterUrl.QR_CODE_SCAN)
class QrCodeScanActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(QrCodeScanFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, QrCodeScanFragment.newInstance(
                    Bundle(intent.extras)
                )
            )
        }
    }
}