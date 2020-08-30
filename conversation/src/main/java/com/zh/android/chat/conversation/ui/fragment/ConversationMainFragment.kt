package com.zh.android.chat.conversation.ui.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.handlerErrorCode
import com.zh.android.base.ext.ioToMain
import com.zh.android.base.ext.lifecycle
import com.zh.android.base.ext.showRequestError
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.conversation.R
import com.zh.android.chat.conversation.http.ConversationPresenter
import com.zh.android.chat.conversation.item.ConversationMainViewBinder
import com.zh.android.chat.conversation.model.Conversation
import com.zh.android.chat.service.module.conversation.ConversationService
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
            register(Conversation::class.java, ConversationMainViewBinder {
                //跳转到会话
                mConversationService?.goConversationChat(
                    fragmentActivity,
                    if (isMeSend(it.fromUser.id)) it.toUser.id else it.fromUser.id,
                    if (isMeSend(it.fromUser.nickname)) it.toUser.nickname else it.fromUser.nickname
                )
            })
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.base_refresh_layout_with_top_bar
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

    override fun setData() {
        super.setData()
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
                        val list = (httpModel.result ?: mutableListOf()).map {
                            it.isMe = isMeSend(it.fromUser.id)
                            it
                        }
                        mListItems.addAll(list)
                        mListAdapter.notifyDataSetChanged()
                    }
                }, { error ->
                    error.printStackTrace()
                    showRequestError()
                    vRefreshLayout.finishRefresh(false)
                })
        }
    }

    /**
     * 是否是我发送的消息
     */
    private fun isMeSend(userId: String): Boolean {
        val myUserId = mLoginService?.getUserId() ?: ""
        return myUserId == userId
    }
}