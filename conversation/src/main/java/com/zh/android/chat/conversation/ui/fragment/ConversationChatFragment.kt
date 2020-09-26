package com.zh.android.chat.conversation.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.apkfuns.logutils.LogUtils
import com.draggable.library.extension.ImageViewerHelper
import com.linghit.base.util.argument.bindArgument
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.ClipboardUtil
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.util.takephoto.RxTakePhoto
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.conversation.R
import com.zh.android.chat.conversation.WebSocketAgent
import com.zh.android.chat.conversation.http.ConversationPresenter
import com.zh.android.chat.conversation.item.*
import com.zh.android.chat.conversation.ui.widget.ChatInputBar
import com.zh.android.chat.conversation.ws.MsgParser
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.module.base.UploadPresenter
import com.zh.android.chat.service.module.conversation.enums.ChatMsgType
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
    private val vChatInputBar: ChatInputBar by bindView(R.id.input_bar)
    private val vTakePhoto: TextView by bindView(R.id.take_photo)
    private val vTakeGallery: TextView by bindView(R.id.take_gallery)

    /**
     * 好友信息
     */
    private val mFriendUserId: String by bindArgument(AppConstant.Key.USER_ID, "")

    /**
     * 某个聊天记录的Id，一般是从会话首页进入时，传过来，我们对记录进行已读即可
     */
    private val mChatRecordId: String by bindArgument(AppConstant.Key.CHAT_RECORD_ID, "")

    private var mCurrentPage: Int = ApiUrl.FIRST_PAGE

    /**
     * 连接地址
     */
    private val mWsUrl = ApiUrl.WS_URL

    private val mRxTakePhoto by lazy {
        RxTakePhoto()
    }
    private val mUploadPresenter by lazy {
        UploadPresenter()
    }

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
            register(ChatRecord::class.java)
                .to(
                    //低版本提示
                    VersionTooLowViewBinder(),
                    //文字
                    TextMsgReceiverViewBinder { position, item ->
                        showTextLongClickDialog(position, item)
                        true
                    },
                    TextMsgSenderViewBinder { position, item ->
                        showTextLongClickDialog(position, item)
                        true
                    },
                    //图片
                    ImageMsgReceiverViewBinder(
                        longClickCallback = { position, item ->
                            showImageLongClickDialog(position, item)
                            true
                        },
                        clickImageCallback = { _, item ->
                            clickImageRecord(item)
                        }
                    ),
                    ImageMsgSenderViewBinder(
                        longClickCallback = { position, item ->
                            showImageLongClickDialog(position, item)
                            true
                        },
                        clickImageCallback = { _, item ->
                            clickImageRecord(item)
                        }
                    )
                ).withClassLinker { _, model ->
                    //我的用户Id
                    val loginUserId = mLoginService?.getUserId() ?: ""
                    if (loginUserId.isBlank()) {
                        VersionTooLowViewBinder::class.java
                    }
                    //消息类型
                    val type = model.type
                    //是否是我的发的
                    val isMe = model.fromUser.id == loginUserId
                    when (type) {
                        ChatMsgType.TEXT.code -> {
                            if (isMe) TextMsgSenderViewBinder::class.java else TextMsgReceiverViewBinder::class.java
                        }
                        ChatMsgType.IMAGE.code -> {
                            if (isMe) ImageMsgSenderViewBinder::class.java else ImageMsgReceiverViewBinder::class.java
                        }
                        else -> {
                            VersionTooLowViewBinder::class.java
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
            setOnRefreshListener {
                loadMore()
            }
            setEnableLoadMore(false)
        }
        vRefreshList.apply {
            layoutManager = LinearLayoutManager(fragmentActivity).apply {
                //布局反转
                reverseLayout = true
                //数据反转
                stackFromEnd = true
            }
            adapter = mListAdapter
        }
        vChatInputBar.setCallback {
            val msgInput = it.trim()
            if (msgInput.isBlank()) {
                toast((R.string.conversation_chat_input_tip))
                return@setCallback
            }
            //发送消息
            sendTextMsg(msgInput)
        }
        vTakePhoto.click {
            val userId = mLoginService?.getUserId() ?: ""
            if (userId.isBlank()) {
                return@click
            }
            //拍照
            mRxTakePhoto.startByCamera(fragmentActivity, false)
                .filter {
                    !it.isTakeCancel
                }
                .map {
                    it.imgPaths[0]
                }
                .flatMap {
                    //上传图片
                    mUploadPresenter.uploadImage(fragmentActivity, it)
                }
                .flatMap { img ->
                    //发送图片消息
                    WebSocketAgent.getRxWebSocket(fragmentActivity)
                        .flatMap {
                            mConversationPresenter.sendImageMsg(
                                it,
                                mWsUrl,
                                userId,
                                mFriendUserId,
                                img
                            )
                        }
                }
                .lifecycle(lifecycleOwner)
                .subscribe({
                    if (it) {
                        LogUtils.d("发送图片消息成功")
                    } else {
                        LogUtils.d("发送图片消息失败")
                    }
                }, {
                    it.printStackTrace()
                    showRequestError()
                })
        }
        vTakeGallery.click {
            //相册中选择
            val userId = mLoginService?.getUserId() ?: ""
            if (userId.isBlank()) {
                return@click
            }
            val count = 6
            mRxTakePhoto.startByGallery(fragmentActivity, count, false)
                .filter {
                    !it.isTakeCancel
                }
                .flatMap {
                    //上传图片
                    mUploadPresenter.uploadMultipleImage(fragmentActivity, it.imgPaths)
                }
                .flatMap {
                    Observable.fromIterable(it)
                }
                .flatMap { img ->
                    //发送图片消息
                    WebSocketAgent.getRxWebSocket(fragmentActivity)
                        .flatMap {
                            mConversationPresenter.sendImageMsg(
                                it,
                                mWsUrl,
                                userId,
                                mFriendUserId,
                                img
                            )
                        }
                }
                .lifecycle(lifecycleOwner)
                .subscribe({
                    if (it) {
                        LogUtils.d("发送图片消息成功")
                    } else {
                        LogUtils.d("发送图片消息失败")
                    }
                }, {
                    it.printStackTrace()
                    showRequestError()
                })
        }
    }

    override fun setData() {
        super.setData()
        getUserInfo()
        refresh()
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

    private fun refresh() {
        mCurrentPage = ApiUrl.FIRST_PAGE
        loadChatRecordList(mCurrentPage)
    }

    private fun loadMore() {
        val nextPage = mCurrentPage + 1
        loadChatRecordList(nextPage)
    }

    /**
     * 拉取历史消息
     */
    private fun loadChatRecordList(
        pageNum: Int
    ) {
        mLoginService?.run {
            val userId = getUserId()
            if (mFriendUserId.isNotBlank()) {
                val isFirstPage = pageNum == ApiUrl.FIRST_PAGE
                val pageSize = 8
                mConversationPresenter.getChatRecordList(userId, mFriendUserId, pageNum, pageSize)
                    .ioToMain()
                    .lifecycle(lifecycleOwner)
                    .subscribe({ httpModel ->
                        if (handlerErrorCode(httpModel)) {
                            httpModel.data?.list?.let { resultList ->
                                //数据为空
                                if (resultList.isEmpty()) {
                                    if (isFirstPage) {
                                        mListItems.clear()
                                        mListAdapter.notifyDataSetChanged()
                                        vRefreshLayout.finishRefresh()
                                    } else {
                                        vRefreshLayout.finishRefresh()
                                    }
                                } else {
                                    mListItems.apply {
                                        if (isFirstPage) {
                                            mListItems.clear()
                                        }
                                        addAll(resultList)
                                        if (isFirstPage) {
                                            scrollToBottom()
                                        }
                                    }
                                    mListAdapter.notifyDataSetChanged()
                                    //最后一页
                                    if (resultList.size < pageSize) {
                                        //没有更新了，不能继续拉取
                                        vRefreshLayout.finishRefresh()
                                        vRefreshLayout.setEnableRefresh(false)
                                    } else {
                                        if (isFirstPage) {
                                            vRefreshLayout.finishRefresh()
                                        } else {
                                            mCurrentPage++
                                            vRefreshLayout.finishRefresh()
                                        }
                                    }
                                }
                            }
                            return@subscribe
                        }
                        //数据异常
                        if (isFirstPage) {
                            if (mListItems.isEmpty()) {
                                vRefreshLayout.finishRefresh(false)
                            } else {
                                vRefreshLayout.finishRefresh(false)
                            }
                        } else {
                            vRefreshLayout.finishRefresh(false)
                        }
                    }, { error ->
                        error.printStackTrace()
                        vRefreshLayout.finishRefresh(false)
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
            override fun onReceiveTextMsg(chatRecord: ChatRecord) {
                insertMsg(chatRecord)
            }

            override fun onReceiveImageMsg(chatRecord: ChatRecord) {
                insertMsg(chatRecord)
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
                if (!it.stringMsg.isNullOrBlank()) {
                    LogUtils.json(it.stringMsg)
                }
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
                    //清空输入框
                    vChatInputBar.setInputText("")
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
     * 删除一条聊天记录
     */
    private fun deleteChatRecord(
        position: Int,
        recordId: String
    ) {
        mConversationPresenter.deleteChatRecord(recordId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    toast(R.string.base_delete_success)
                    mListItems.remove(mListItems[position])
                    mListAdapter.fixNotifyItemRemoved(position)
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }

    /**
     * 插入一条消息
     */
    private fun insertMsg(chatRecord: ChatRecord) {
        //插入到最前面
        mListItems.add(0, chatRecord)
        mListAdapter.notifyDataSetChanged()
        //滚动到最底下
        scrollToBottom()
    }

    /**
     * 滚动列表到底部
     */
    private fun scrollToBottom() {
        vRefreshList.scrollToPosition(0)
    }

    /**
     * 点击了图片记录
     */
    private fun clickImageRecord(item: ChatRecord) {
        val currentImageUrl = ApiUrl.getFullImageUrl(item.image!!.image)
        //收集所有图片Url
        val imageUrls = mListItems.filterIsInstance<ChatRecord>()
            .filter {
                it.type == ChatMsgType.IMAGE.code && it.image != null
            }.map {
                ApiUrl.getFullImageUrl(it.image!!.image)
            }.toList()
        //找到当前条目在列表中的位置
        val index = imageUrls.indexOf(currentImageUrl)
        if (index == -1) {
            return
        }
        //跳转到图片预览
        ImageViewerHelper.showImages(
            fragmentActivity,
            imageUrls, index = index
        )
    }

    /**
     * 显示文字长按提示弹窗
     */
    private fun showTextLongClickDialog(position: Int, item: ChatRecord) {
        AlertDialog.Builder(fragmentActivity)
            .setItems(
                arrayOf(
                    getString(R.string.base_copy),
                    getString(R.string.base_delete)
                )
            ) { _, which ->
                when (which) {
                    0 -> {
                        item.text?.content.let {
                            ClipboardUtil.copyToClipboard(fragmentActivity, it)
                            toast(R.string.base_copy_success)
                        }
                    }
                    1 -> {
                        deleteChatRecord(position, item.id)
                    }
                }
            }
            .create()
            .show()
    }

    /**
     * 显示图片长按提示弹窗
     */
    private fun showImageLongClickDialog(position: Int, item: ChatRecord) {
        AlertDialog.Builder(fragmentActivity)
            .setItems(
                arrayOf(
                    getString(R.string.base_delete)
                )
            ) { _, which ->
                when (which) {
                    0 -> {
                        deleteChatRecord(position, item.id)
                    }
                }
            }
            .create()
            .show()
    }
}