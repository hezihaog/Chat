package com.zh.android.chat.discovery.ui.fragment

import android.content.Context.VIBRATOR_SERVICE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.view.View
import cn.bingoogolapple.qrcode.core.QRCodeView
import cn.bingoogolapple.qrcode.zxing.ZXingView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.apkfuns.logutils.LogUtils
import com.blankj.utilcode.util.RegexUtils
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.ext.toast
import com.zh.android.base.util.web.BrowserActivity
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.discovery.R
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.module.friend.FriendService
import kotterknife.bindView
import java.net.URI


/**
 * @author wally
 * @date 2020/09/09
 * 二维码扫描
 */
class QrCodeScanFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.FRIEND_SERVICE)
    var mFriendService: FriendService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vScanView: ZXingView by bindView(R.id.scan_view)

    private val mMainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): QrCodeScanFragment {
            val fragment = QrCodeScanFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        //打开后置摄像头开始预览，但并未开始识别
        vScanView.startCamera()
        //显示扫描框，并开始识别
        vScanView.startSpotAndShowRect()
    }

    override fun onStop() {
        super.onStop()
        //关闭摄像头预览，并隐藏扫描框
        vScanView.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMainHandler.removeCallbacksAndMessages(null)
        //销毁二维码扫描组件
        vScanView.onDestroy()
    }

    override fun onInflaterViewId(): Int {
        return R.layout.discovery_qr_code_scan_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.discovery_scan_qrcode)
        }
        vScanView.setDelegate(object : QRCodeView.Delegate {
            override fun onScanQRCodeSuccess(result: String?) {
                //扫描成功
                if (result.isNullOrBlank()) {
                    return
                }
                LogUtils.d("扫描结果：$result")
                //震动一下
                vibrate()
                //解析结果
                parseScanResult(result)
                //延迟一下，再继续识别
                mMainHandler.postDelayed({
                    vScanView.startSpot()
                }, 1000)
            }

            override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
                //当前扫描环境过暗
                var tipText: String = vScanView.scanBoxView.tipText ?: ""
                val ambientBrightnessTip = getString(R.string.discovery_ambient_brightness_tip)
                if (isDark) {
                    if (!tipText.contains(ambientBrightnessTip)) {
                        vScanView.scanBoxView.tipText = tipText + ambientBrightnessTip
                    }
                } else {
                    if (tipText.contains(ambientBrightnessTip)) {
                        tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip))
                        vScanView.scanBoxView.tipText = tipText
                    }
                }
            }

            override fun onScanQRCodeOpenCameraError() {
                toast(R.string.discovery_open_camera_fail)
                fragmentActivity.finish()
            }
        })
    }

    /**
     * 解析扫描结果
     */
    private fun parseScanResult(result: String) {
        val uri = URI(result)
        when {
            //用户二维码，跳转到用户页面
            uri.scheme == ApiUrl.QR_CODE_SCHEME -> {
                val path = uri.path
                val query = uri.query
                if (path != ApiUrl.QR_CODE_USER_PATH) {
                    return
                }
                //按等号，拆分参数
                val queryArray = query.split("=")
                //将参数数组转为Map，注意步长要为2
                val queryMap = mutableMapOf<String, String>().apply {
                    for (index in queryArray.indices step 2) {
                        val key = queryArray[index]
                        val value = queryArray[index + 1]
                        put(key, value)
                    }
                }
                //获取传过来的UserId
                val userId = queryMap[AppConstant.Key.USER_ID]
                if (userId.isNullOrBlank()) {
                    return
                }
                //跳转到用户详情
                mFriendService?.goUserProfile(fragmentActivity, userId)
            }
            //普通网页
            RegexUtils.isURL(result) -> {
                BrowserActivity.start(fragmentActivity, result)
            }
            //不能识别类型
            else -> {
                toast(getString(R.string.discovery_not_user_qr_core_tip))
            }
        }
    }

    /**
     * 扫描后震动一下
     */
    private fun vibrate() {
        (fragmentActivity.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(200)
    }
}