package com.zh.android.chat.discovery.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.loadMainFragment
import com.zh.android.chat.discovery.R
import com.zh.android.chat.discovery.ui.fragment.QrCodeScanHistoryFragment

/**
 * @author wally
 * @date 2021/02/21
 * 二维码扫描历史
 */
@Route(path = ARouterUrl.QR_CODE_SCAN_HISTORY)
class QrCodeScanHistoryActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        loadMainFragment(R.id.base_container, QrCodeScanHistoryFragment.newInstance())
    }
}