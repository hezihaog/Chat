package com.zh.android.chat.discovery.ui.fragment

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.apkfuns.logutils.LogUtils
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.ext.lifecycle
import com.zh.android.base.ext.longClick
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.discovery.R
import com.zh.android.chat.service.module.discovery.DiscoveryService
import com.zh.android.chat.service.module.friend.FriendService
import com.zh.android.chat.service.module.mall.MallService
import com.zh.android.chat.service.module.moment.MomentService
import com.zh.android.chat.service.module.todo.TodoService
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

    @JvmField
    @Autowired(name = ARouterUrl.MALL_SERVICE)
    var mMallService: MallService? = null

    @JvmField
    @Autowired(name = ARouterUrl.TODO_SERVICE)
    var mTodoService: TodoService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vMomentLayout: View by bindView(R.id.moment_layout)
    private val vMomentVideoLayout: View by bindView(R.id.moment_video_layout)
    private val vMomentMallLayout: View by bindView(R.id.moment_mall_layout)
    private val vAddFriendLayout: View by bindView(R.id.add_friend_layout)
    private val vScanQrcodeLayout: View by bindView(R.id.scan_qrcode_layout)
    private val vTodoLayout: View by bindView(R.id.todo_layout)

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
        vMomentVideoLayout.click {
            //只有视频的动态
            mMomentService?.goMomentVideoList(fragmentActivity)
        }
        vMomentMallLayout.click {
            //原生商城
            mMallService?.goMall(fragmentActivity)
        }
        vMomentMallLayout.longClick {
            //网页商城
            mMallService?.goMallWeb(fragmentActivity)
            true
        }
        //添加好友
        vAddFriendLayout.click {
            mFriendService?.run {
                goAddFriend(fragmentActivity)
            }
        }
        //扫一扫
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
        //待办事项
        vTodoLayout.click {
            mTodoService?.goTodoList(fragmentActivity)
        }
    }
}