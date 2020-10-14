package com.zh.android.chat.conversation.ui.fragment

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.conversation.R
import com.zh.android.chat.conversation.http.ConversationPresenter
import com.zh.android.chat.conversation.item.ConversationMainViewBinder
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.conversation.ConversationService
import com.zh.android.chat.service.module.conversation.model.Conversation
import com.zh.android.chat.service.module.login.LoginService
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/08/26
 * 会话首页
 */
class ConversationMainFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    @JvmField
    @Autowired(name = ARouterUrl.CONVERSATION_SERVICE)
    var mConversationService: ConversationService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    private val mConversationPresenter by lazy {
        ConversationPresenter()
    }

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(Conversation::class.java, ConversationMainViewBinder(
                itemClickCallback = {
                    //跳转到会话
                    mConversationService?.goConversationChat(
                        fragmentActivity,
                        if (isMeSend(it.fromUser.id)) it.toUser.id else it.fromUser.id,
                        if (isMeSend(it.fromUser.id)) it.toUser.nickname else it.fromUser.nickname
                    )
                },
                longClickCallback = { position, item ->
                    AlertDialog.Builder(fragmentActivity)
                        .setItems(
                            arrayOf(
                                getString(R.string.base_delete)
                            )
                        ) { _, which ->
                            when (which) {
                                //删除会话
                                0 -> {
                                    val myUserId = mLoginService?.getUserId()
                                    val friendUserId = when {
                                        item.fromUser.id == myUserId -> {
                                            item.toUser.id
                                        }
                                        item.toUser.id == myUserId -> {
                                            item.fromUser.id
                                        }
                                        else -> {
                                            ""
                                        }
                                    }
                                    if (friendUserId.isNotBlank()) {
                                        deleteConversation(position, friendUserId)
                                    }
                                }
                            }
                        }
                        .create()
                        .show()
                    true
                }
            ))
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.conversation_main_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            setTitle(getString(R.string.conversation_module_name))
        }
        vRefreshLayout.apply {
            setEnableLoadMore(false)
            setOnRefreshListener {
                refresh()
            }
        }
        vRefreshList.apply {
            layoutManager = LinearLayoutManager(fragmentActivity)
            adapter = mListAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    /**
     * 刷新
     */
    private fun refresh() {
        mLoginService?.run {
            val userId = getUserId()
            mConversationPresenter.getAllConversation(userId)
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({ httpModel ->
                    vRefreshLayout.finishRefresh()
                    if (handlerErrorCode(httpModel)) {
                        mListItems.clear()
                        val list = (httpModel.data ?: mutableListOf()).map {
                            it.isMe = isMeSend(it.fromUser.id)
                            it
                        }
                        mListItems.addAll(list)
                        mListAdapter.notifyDataSetChanged()
                    }
                }, { error ->
                    error.printStackTrace()
                    vRefreshLayout.finishRefresh(false)
                })
        }
    }

    /**
     * 删除和指定好友的整个会话
     */
    private fun deleteConversation(
        position: Int,
        friendUserId: String
    ) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mConversationPresenter.deleteConversation(userId, friendUserId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    toast(R.string.base_delete_success)
                    mListItems.removeAt(position)
                    mListAdapter.fixNotifyItemRemoved(position)
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }

    /**
     * 是否是我发送的消息
     */
    private fun isMeSend(userId: String): Boolean {
        val myUserId = mLoginService?.getUserId() ?: ""
        return myUserId == userId
    }
}