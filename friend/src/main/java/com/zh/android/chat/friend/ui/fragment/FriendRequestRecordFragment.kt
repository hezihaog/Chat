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
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.friend.R
import com.zh.android.chat.friend.http.FriendPresenter
import com.zh.android.chat.friend.item.FriendRequestViewBinder
import com.zh.android.chat.friend.model.FriendRequest
import com.zh.android.chat.service.module.login.LoginService
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/08/27
 * 好友申请记录
 */
class FriendRequestRecordFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    companion object {
        fun newInstance(args: Bundle? = Bundle()): FriendRequestRecordFragment {
            val fragment = FriendRequestRecordFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val mFriendPresenter by lazy {
        FriendPresenter()
    }

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            //好友申请记录条目
            register(FriendRequest::class.java, FriendRequestViewBinder({ position, item ->
                //忽略好友请求
                mFriendPresenter.ignoreFriendRequest(item.id)
                    .ioToMain()
                    .lifecycle(lifecycleOwner)
                    .subscribe({ httpModel ->
                        if (handlerErrorCode(httpModel)) {
                            mListItems.removeAt(position)
                            fixNotifyItemRemoved(position)
                            toast(R.string.friend_request_operation_success)
                        }
                    }, { error ->
                        error.printStackTrace()
                        showRequestError()
                    })
            }, { position, item ->
                //接受好友请求
                mFriendPresenter.acceptFriendRequest(item.id)
                    .ioToMain()
                    .lifecycle(lifecycleOwner)
                    .subscribe({ httpModel ->
                        if (handlerErrorCode(httpModel)) {
                            mListItems.removeAt(position)
                            fixNotifyItemRemoved(position)
                            toast(R.string.friend_request_operation_success)
                        }
                    }, { error ->
                        error.printStackTrace()
                        showRequestError()
                    })
            }))
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
            setTitle(getString(R.string.friend_request))
        }
        vRefreshLayout.apply {
            setOnRefreshListener {
                refresh()
            }
        }
        vRefreshList.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mListAdapter
        }
    }

    override fun setData() {
        super.setData()
        refresh()
    }

    private fun refresh() {
        getAllFriendRequest()
    }

    /**
     * 获取我所有的好友请求
     */
    private fun getAllFriendRequest() {
        mLoginService?.run {
            val userId = getUserId()
            mFriendPresenter.getUserAllFriendRequest(userId)
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({ httpModel ->
                    vRefreshLayout.finishRefresh()
                    if (handlerErrorCode(httpModel)) {
                        val list = httpModel.result ?: mutableListOf()
                        mListItems.clear()
                        mListItems.addAll(list)
                        mListAdapter.notifyDataSetChanged()
                    }
                }, { error ->
                    error.printStackTrace()
                    vRefreshLayout.finishRefresh(false)
                })
        }
    }
}