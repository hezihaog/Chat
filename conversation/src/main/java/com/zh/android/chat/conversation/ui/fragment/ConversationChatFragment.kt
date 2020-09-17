package com.zh.android.chat.conversation.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.apkfuns.logutils.LogUtils
import com.linghit.base.util.argument.bindArgument
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.conversation.R
import com.zh.android.chat.conversation.WebSocketAgent
import com.zh.android.chat.conversation.http.ConversationPresenter
import com.zh.android.chat.conversation.item.TextMsgReceiverViewBinder
import com.zh.android.chat.conversation.item.TextMsgSenderViewBinder
import com.zh.android.chat.conversation.ws.MsgParser
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.module.conversation.model.ChatRecord
import com.zh.android.chat.service.module.login.LoginService
import com.zh.android.chat.service.module.mine.MineService
import io.reactivex.Observable
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/08/28
 * 聊天会话页面
 */
class ConversationChatFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    @JvmField
    @Autowired(name = ARouterUrl.MINE_SERVICE)
    var mMineService: MineService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)
    private val vMsgInput: EditText by bindView(R.id.msg_input)
    private val vSend: TextView by bindView(R.id.send)

    /**
     * 好友信息
     */
    private val mFriendUserId: String by bindArgument(AppConstant.Key.USER_ID, "")

    /**
     * 某个聊天记录的Id，一般是从会话首页进入时，传过来，我们对记录进行已读即可
     */
    private val mChatRecordId: String by bindArgument(AppConstant.Key.CHAT_RECORD_ID, "")

    /**
     * 连接地址
     */
    private val mWsUrl = ApiUrl.WS_URL

    private val mWaitController by lazy {
        WaitLoadingController(fragmentActivity, lifecycleOwner)
    }

    private val mConversationPresenter by lazy {
        ConversationPresenter()
    }

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(ChatRecord::class.java).to(
                TextMsgReceiverViewBinder(),
                TextMsgSenderViewBinder()
            ).withClassLinker { _, model ->
                //我的用户Id
                val loginUserId = mLoginService?.getUserId() ?: ""
                //一般不会出现这种情况，登录模块是必须有的
                return@withClassLinker if (loginUserId.isBlank()) {
                    TextMsgSenderViewBinder::class.java
                } else {
                    //是否是我的发的
                    val isMe = model.fromUserId == loginUserId
                    if (isMe) {
                        TextMsgSenderViewBinder::class.java
                    } else {
                        TextMsgReceiverViewBinder()::class.java
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): ConversationChatFragment {
            val fragment = ConversationChatFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.conversation_chat_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.conversation_connection_ing)
        }
        vRefreshLayout.apply {
            setEnableRefresh(false)
            setEnableLoadMore(false)
        }
        vRefreshList.apply {
            val manager = LinearLayoutManager(fragmentActivity)
            layoutManager = manager
            adapter = mListAdapter
            manager.apply {
                //翻转Rv布局
                stackFromEnd = true
            }
        }
        vSend.click {
            val msgInput = vMsgInput.text.toString().trim()
            if (msgInput.isBlank()) {
                toast((R.string.conversation_chat_input_tip))
                return@click
            }
            //发送消息
            sendTextMsg(msgInput)
        }
    }

    override fun setData() {
        super.setData()
        getUserInfo()
        loadChatRecordList()
        connectionChatServer()
    }

    /**
     * 获取用户信息
     */
    private fun getUserInfo() {
        mMineService?.run {
            getUserInfo(mFriendUserId)
                .doOnSubscribeUi {
                    mWaitController.showWait()
                }
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({ httpModel ->
                    mWaitController.hideWait()
                    if (handlerErrorCode(httpModel)) {
                        httpModel.data?.let {
                            vTopBar.setTitle(it.nickname)
                        }
                    }
                }, { error ->
                    error.printStackTrace()
                    showRequestError()
                    mWaitController.hideWait()
                })
        }
    }

    /**
     * 拉取历史消息
     */
    private fun loadChatRecordList() {
        mLoginService?.run {
            val userId = getUserId()
            if (mFriendUserId.isNotBlank()) {
                mConversationPresenter.getChatRecordList(userId, mFriendUserId)
                    .ioToMain()
                    .lifecycle(lifecycleOwner)
                    .subscribe({ httpModel ->
                        if (handlerErrorCode(httpModel)) {
                            val list = httpModel.data ?: mutableListOf()
                            mListItems.addAll(list)
                            mListAdapter.notifyDataSetChanged()
                        }
                    }, { error ->
                        error.printStackTrace()
                    })
            }
        }
    }

    /**
     * 连接聊天服务
     */
    private fun connectionChatServer() {
        //开始解析消息
        MsgParser(fragmentActivity, mWsUrl, object : MsgParser.OnReceiveMsgCallback {
            override fun onReceiveTextMsg(record: ChatRecord) {
                insertMsg(record)
            }
        }).listener()
            .flatMap {
                //连接成功
                if (it.isConnect) {
                    //绑定连接
                    bindConnection()
                    //已读消息
                    readLastMsg()
                    //发送心跳
                    sendHeartBeatMsg()
                }
                Observable.just(it)
            }
            .lifecycle(lifecycleOwner)
            .subscribe({
                LogUtils.json(it.stringMsg)
            }, { error ->
                error.printStackTrace()
            })
    }

    /**
     * 绑定连接
     */
    private fun bindConnection() {
        mLoginService?.run {
            val userId = getUserId()
            if (userId.isBlank()) {
                return@run
            }
            WebSocketAgent.getRxWebSocket(fragmentActivity)
                .flatMap {
                    mConversationPresenter.bindConnection(it, mWsUrl, userId)
                }
                .lifecycle(lifecycleOwner)
                .subscribe({
                    if (it) {
                        LogUtils.d("绑定连接成功")
                    } else {
                        LogUtils.d("绑定连接失败")
                    }
                }, { error ->
                    error.printStackTrace()
                    LogUtils.d("绑定连接失败，异常：${error.message}")
                })
        }
    }

    /**
     * 发送心跳
     */
    private fun sendHeartBeatMsg() {
        WebSocketAgent.getRxWebSocket(fragmentActivity)
            .flatMap {
                mConversationPresenter.sendHeartBeatMsg(it, mWsUrl)
            }
            .lifecycle(lifecycleOwner)
            .subscribe({
                if (it) {
                    LogUtils.d("发送心跳成功")
                } else {
                    LogUtils.d("发送心跳失败")
                }
            }, { error ->
                error.printStackTrace()
                LogUtils.d("发送心跳失败，异常：${error.message}")
            })
    }

    /**
     * 发送文本消息
     */
    private fun sendTextMsg(text: String) {
        mLoginService?.run {
            val userId = getUserId()
            if (userId.isBlank()) {
                return@run
            }
            WebSocketAgent.getRxWebSocket(fragmentActivity)
                .flatMap {
                    mConversationPresenter.sendTextMsg(
                        it,
                        mWsUrl,
                        userId,
                        mFriendUserId,
                        text
                    )
                }
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({
                    //插入一条消息
                    insertMsg(it)
                    //清空输入框
                    vMsgInput.setText("")
                    LogUtils.d("发送文本消息成功：$text")
                }, { error ->
                    error.printStackTrace()
                    LogUtils.d("发送文本消息失败：$text")
                })
        }
    }

    /**
     * 已读最后一条消息
     */
    private fun readLastMsg() {
        if (mChatRecordId.isNotBlank()) {
            WebSocketAgent.getRxWebSocket(fragmentActivity)
                .flatMap {
                    mConversationPresenter.readMsg(
                        it,
                        mWsUrl,
                        mChatRecordId
                    )
                }
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({
                    if (it) {
                        LogUtils.d("已读消息成功，记录Id：$mChatRecordId")
                    } else {
                        LogUtils.d("已读消息失败，记录Id：$mChatRecordId")
                    }
                }, { error ->
                    error.printStackTrace()
                    LogUtils.d("已读消息失败，记录Id：${mChatRecordId}，异常：${error.message}")
                })
        }
    }

    /**
     * 插入一条消息
     */
    private fun insertMsg(record: ChatRecord) {
        mListItems.add(record)
        mListAdapter.notifyDataSetChanged()
        //滚动到最底下
        vRefreshList.scrollToPosition(mListItems.size - 1)
    }
}