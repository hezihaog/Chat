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
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.ext.ioToMain
import com.zh.android.base.ext.lifecycle
import com.zh.android.base.ext.toast
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.discovery.R
import com.zh.android.chat.discovery.http.DiscoveryPresenter
import com.zh.android.chat.discovery.util.QrCodeUtil
import com.zh.android.chat.service.module.discovery.DiscoveryService
import com.zh.android.chat.service.module.friend.FriendService
import com.zh.android.chat.service.module.login.LoginService
import kotterknife.bindView


/**
 * @author wally
 * @date 2020/09/09
 * 二维码扫描
 */
class QrCodeScanFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    @JvmField
    @Autowired(name = ARouterUrl.FRIEND_SERVICE)
    var mFriendService: FriendService? = null

    @JvmField
    @Autowired(name = ARouterUrl.DISCOVERY_SERVICE)
    var mDiscoveryService: DiscoveryService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vScanView: ZXingView by bindView(R.id.scan_view)

    private val mMainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val mDiscoveryPresenter by lazy {
        DiscoveryPresenter()
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
            addRightTextButton(R.string.discovery_qr_scan_history, R.id.discovery_qr_scan_history)
                .click {
                    //跳转到扫描历史
                    mDiscoveryService?.goQrCodeScanHistory(fragmentActivity)
                }
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
                }, 1500)
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
        //保存扫描记录
        saveScanHistory(result)
        //判断二维码内容的类型
        QrCodeUtil.parseScanResult(result, {
            //跳转到用户详情
            mFriendService?.goUserProfile(fragmentActivity, it)
            fragmentActivity.finish()
        }, {
            //普通网页
            mFriendService?.goInnerWebBrowser(fragmentActivity, it)
            fragmentActivity.finish()
        }, {
            //不能识别，跳转到结果页面直接显示
            mDiscoveryService?.goQrCodeScanResult(fragmentActivity, it)
        })
    }

    /**
     * 保存扫描记录到数据库中
     * @param qrCodeContent 二维码内容
     */
    private fun saveScanHistory(qrCodeContent: String) {
        mLoginService?.getUserId().let { userId ->
            if (userId.isNullOrBlank()) {
                return@let
            }
            mDiscoveryPresenter.saveScanHistory(userId, qrCodeContent)
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({
                    if (it) {
                        LogUtils.d("保存二维码扫描记录到数据库，成功")
                    } else {
                        LogUtils.d("保存二维码扫描记录到数据库，失败")
                    }
                }, {
                    it.printStackTrace()
                    LogUtils.d("保存二维码扫描记录到数据库，异常 ${it.message}")
                })
        }
    }

    /**
     * 扫描后震动一下
     */
    private fun vibrate() {
        (fragmentActivity.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(200)
    }
}