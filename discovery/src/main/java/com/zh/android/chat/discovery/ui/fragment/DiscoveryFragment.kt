package com.zh.android.chat.discovery.ui.fragment

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.apkfuns.logutils.LogUtils
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.ext.lifecycle
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.discovery.R
import com.zh.android.chat.service.module.discovery.DiscoveryService
import com.zh.android.chat.service.module.friend.FriendService
import com.zh.android.chat.service.module.moment.MomentService
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/08/26
 * 发现首页
 */
class DiscoveryFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.FRIEND_SERVICE)
    var mFriendService: FriendService? = null

    @JvmField
    @Autowired(name = ARouterUrl.DISCOVERY_SERVICE)
    var mDiscoveryService: DiscoveryService? = null

    @JvmField
    @Autowired(name = ARouterUrl.MOMENT_SERVICE)
    var mMomentService: MomentService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vMomentLayout: View by bindView(R.id.friend_moment_layout)
    private val vAddFriendLayout: View by bindView(R.id.add_friend_layout)
    private val vScanQrcodeLayout: View by bindView(R.id.scan_qrcode_layout)

    override fun onInflaterViewId(): Int {
        return R.layout.discovery_main_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            setTitle(getString(R.string.discovery_module_name))
        }
        vMomentLayout.click {
            mMomentService?.goMomentList(fragmentActivity)
        }
        vAddFriendLayout.click {
            mFriendService?.run {
                goAddFriend(fragmentActivity)
            }
        }
        vScanQrcodeLayout.click {
            mDiscoveryService?.run {
                goQrCodeScan(fragmentActivity)
                    .lifecycle(lifecycleOwner)
                    .subscribe({
                        LogUtils.d("跳转二维码扫描：成功")
                    }, {
                        it.printStackTrace()
                        LogUtils.d("跳转二维码扫描：失败")
                    })
            }
        }
    }
}