package com.zh.android.chat.discovery.ui.fragment

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.ext.toast
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.discovery.R
import com.zh.android.chat.service.module.friend.FriendService
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

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vFriendCircleLayout: View by bindView(R.id.friend_circle_layout)
    private val vAddFriendLayout: View by bindView(R.id.add_friend_layout)
    private val vScanQrcodeLayout: View by bindView(R.id.scan_qrcode_layout)

    override fun onInflaterViewId(): Int {
        return R.layout.discovery_main_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            setTitle(getString(R.string.discovery_module_name))
        }
        vFriendCircleLayout.click {
            toast("跳转到朋友圈")
        }
        vAddFriendLayout.click {
            mFriendService?.run {
                goAddFriend(fragmentActivity)
            }
        }
        vScanQrcodeLayout.click {
            toast("扫描二维码")
        }
    }
}