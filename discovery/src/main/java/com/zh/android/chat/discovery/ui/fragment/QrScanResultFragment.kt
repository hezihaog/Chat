package com.zh.android.chat.discovery.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.linghit.base.util.argument.bindArgument
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.discovery.R
import com.zh.android.chat.service.AppConstant
import kotterknife.bindView

/**
 * @author wally
 * @date 2021/02/21
 * 二维码扫描结果页面
 */
class QrScanResultFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vResult: TextView by bindView(R.id.result)

    /**
     * 扫描结果
     */
    private val mScanResult: String by bindArgument(AppConstant.Key.QR_CODE_SCAN_RESULT, "")

    companion object {
        fun newInstance(args: Bundle? = Bundle()): QrScanResultFragment {
            val fragment = QrScanResultFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.discovery_qr_code_scan_result_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.discovery_qr_scan_result)
        }
        vResult.text = mScanResult
    }
}