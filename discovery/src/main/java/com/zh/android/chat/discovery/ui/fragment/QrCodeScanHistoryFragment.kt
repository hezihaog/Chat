package com.zh.android.chat.discovery.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.discovery.R
import com.zh.android.chat.discovery.http.DiscoveryPresenter
import com.zh.android.chat.discovery.item.QrCodeScanHistoryBinder
import com.zh.android.chat.discovery.model.QrCodeScanHistoryModel
import com.zh.android.chat.discovery.util.QrCodeUtil
import com.zh.android.chat.service.module.discovery.DiscoveryService
import com.zh.android.chat.service.module.friend.FriendService
import com.zh.android.chat.service.module.login.LoginService
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2021/02/21
 * 二维码扫描历史
 */
class QrCodeScanHistoryFragment : BaseFragment() {
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
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    private val mWaitController by lazy {
        WaitLoadingController(fragmentActivity, fragment)
    }

    private val mDiscoveryPresenter by lazy {
        DiscoveryPresenter()
    }

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(QrCodeScanHistoryModel::class.java, QrCodeScanHistoryBinder({ _, model ->
                //判断类型，跳转对应的页面
                QrCodeUtil.parseScanResult(model.qrCodeContent, {
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
            }, { position, model ->
                //长按显示删除
                AlertDialog.Builder(fragmentActivity)
                    .setItems(
                        arrayOf(
                            getString(R.string.base_delete)
                        )
                    ) { _, which ->
                        if (which == 0) {
                            val userId = mLoginService?.getUserId() ?: ""
                            if (userId.isBlank()) {
                                return@setItems
                            }
                            //删除
                            mDiscoveryPresenter.deleteScanHistoryById(userId, model.id)
                                .doOnSubscribeUi {
                                    mWaitController.showWait()
                                }
                                .ioToMain()
                                .lifecycle(lifecycleOwner)
                                .subscribe({
                                    mWaitController.hideWait()
                                    if (it) {
                                        mListItems.remove(model)
                                        fixNotifyItemRemoved(position)
                                        toast(R.string.base_delete_success)
                                    } else {
                                        toast(R.string.base_delete_fail)
                                    }
                                }, {
                                    it.printStackTrace()
                                    toast(R.string.base_delete_fail)
                                    mWaitController.hideWait()
                                })
                        }
                    }
                    .create()
                    .show()
                true
            }))
        }
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): QrCodeScanHistoryFragment {
            val fragment = QrCodeScanHistoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.base_refresh_layout_with_top_bar
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.discovery_qr_scan_history)
        }
        vRefreshLayout.apply {
            setOnRefreshListener {
                refresh()
            }
            setEnableLoadMore(false)
        }
        vRefreshList.apply {
            layoutManager = LinearLayoutManager(fragmentActivity)
            adapter = mListAdapter
        }
    }

    override fun setData() {
        super.setData()
        refresh()
    }

    private fun refresh() {
        mLoginService?.getUserId()?.let { userId ->
            mDiscoveryPresenter.getScanHistoryList(userId)
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({
                    //数据库模型转换为页面模型
                    val list = it.map { entity ->
                        QrCodeScanHistoryModel(
                            entity.id,
                            entity.createTime,
                            entity.userId,
                            entity.qrCodeContent
                        )
                    }
                    mListItems.clear()
                    mListItems.addAll(list)
                    mListAdapter.notifyDataSetChanged()
                    vRefreshLayout.finishRefresh()
                }, {
                    it.printStackTrace()
                    vRefreshLayout.finishRefresh(false)
                })
        }
    }
}