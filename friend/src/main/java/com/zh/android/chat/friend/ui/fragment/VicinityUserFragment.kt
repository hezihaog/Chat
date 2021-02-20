package com.zh.android.chat.friend.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.location.RxLocation
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.friend.R
import com.zh.android.chat.friend.enums.FriendRequestStatus
import com.zh.android.chat.friend.http.FriendPresenter
import com.zh.android.chat.friend.item.VicinityUserViewBinder
import com.zh.android.chat.friend.model.VicinityUserModel
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.login.LoginService
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/06
 * 附近的人
 */
class VicinityUserFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    private val mFriendPresenter by lazy {
        FriendPresenter()
    }

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(VicinityUserModel::class.java, VicinityUserViewBinder { position, item ->
                //检查fromUserId
                val fromUserId = mLoginService?.getUserId() ?: ""
                if (fromUserId.isBlank()) {
                    return@VicinityUserViewBinder
                }
                //检查toUserId
                val toUserId = item.userId
                if (toUserId.isBlank()) {
                    return@VicinityUserViewBinder
                }
                sendFriendRequest(position, fromUserId, toUserId)
            })
        }
    }

    private val mRxLocation by lazy {
        RxLocation()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): VicinityUserFragment {
            val fragment = VicinityUserFragment()
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
            setTitle(R.string.friend_vicinity_user)
        }
        vRefreshLayout.apply {
            setEnableRefresh(false)
            setEnableLoadMore(false)
        }
        vRefreshList.apply {
            layoutManager = LinearLayoutManager(fragmentActivity)
            adapter = mListAdapter
        }
    }

    override fun setData() {
        super.setData()
        mRxLocation.getLocation(fragmentActivity)
            .lifecycle(lifecycleOwner)
            .subscribe({
                if (it.isNotOpenLocation) {
                    toast(R.string.friend_not_open_location)
                    return@subscribe
                }
                getVicinityUserList(it.longitude, it.latitude)
            }, {
                toast("获取位置信息失败，请重试")
            })
    }

    /**
     * 获取附近的人
     */
    private fun getVicinityUserList(
        lon: Double,
        lat: Double
    ) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrEmpty()) {
            return
        }
        mFriendPresenter.getVicinityUserList(userId, lon, lat)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                vRefreshLayout.finishRefresh()
                if (handlerErrorCode(httpModel)) {
                    httpModel.data?.let {
                        mListItems.clear()
                        mListItems.addAll(it)
                        mListAdapter.notifyDataSetChanged()
                    }
                }
            }, {
                it.printStackTrace()
                vRefreshLayout.finishRefresh(false)
                showRequestError()
            })
    }

    /**
     * 发送好友请求
     */
    private fun sendFriendRequest(position: Int, fromUserId: String, toUserId: String) {
        mFriendPresenter.sendFriendRequest(fromUserId, toUserId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    toast(R.string.friend_request_success)
                    val item = mListItems[position]
                    if (item is VicinityUserModel) {
                        item.isSendRequest = true
                        item.status = FriendRequestStatus.WAIT_OP.code
                        mListAdapter.notifyDataSetChanged()
                    }
                }
            }, { error ->
                error.printStackTrace()
                showRequestError()
            })
    }
}