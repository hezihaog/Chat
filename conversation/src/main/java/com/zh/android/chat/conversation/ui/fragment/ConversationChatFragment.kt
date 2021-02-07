package com.zh.android.chat.conversation.ui.fragment

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.apkfuns.logutils.LogUtils
import com.blankj.utilcode.util.RegexUtils
import com.draggable.library.extension.ImageViewerHelper
import com.hule.dashi.mediaplayer.*
import com.linghit.base.util.argument.bindArgument
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.ClipboardUtil
import com.zh.android.base.util.RecyclerViewScrollHelper
import com.zh.android.base.util.VibratorHelper
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.util.rx.RxUtil
import com.zh.android.base.util.takephoto.RxTakePhoto
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.conversation.R
import com.zh.android.chat.conversation.WebSocketAgent
import com.zh.android.chat.conversation.http.ConversationPresenter
import com.zh.android.chat.conversation.item.*
import com.zh.android.chat.conversation.ui.dialog.VoiceRecordDialog
import com.zh.android.chat.conversation.ui.widget.ChatInputBar
import com.zh.android.chat.conversation.ui.widget.VoiceRecordButton
import com.zh.android.chat.conversation.ws.MsgParser
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.module.base.UploadPresenter
import com.zh.android.chat.service.module.base.model.DataIndexModel
import com.zh.android.chat.service.module.conversation.enums.ChatMsgType
import com.zh.android.chat.service.module.conversation.model.ChatRecord
import com.zh.android.chat.service.module.login.LoginService
import com.zh.android.chat.service.module.mine.MineService
import io.reactivex.Observable
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import java.util.concurrent.TimeUnit

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

    private val mVoiceRecordHandler by lazy {
        Handler(Looper.getMainLooper())
    }
    private val mVoiceRecordDialog by lazy {
        VoiceRecordDialog(fragmentActivity)
    }
    private val mRecorderManager by lazy {
        RxMediaRecorder.getManager(context)
    }
    private val mPlayerManager by lazy {
        RxMediaPlayer.getManager(context)
    }
    private val mPlayHelper by lazy {
        ItemVoicePlayHelper(mPlayerManager)
    }

    private val mRxPermissions by lazy {
        RxPermissions(fragment)
    }

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

    private val mRecyclerViewScrollHelper by lazy {
        RecyclerViewScrollHelper().attachRecyclerView(vRefreshList)
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
                    TextMsgReceiverViewBinder({ _, item ->
                        val url = item.text?.content ?: ""
                        if (url.isNotBlank() && RegexUtils.isURL(url)) {
                            mLoginService?.goInnerWebBrowser(fragmentActivity, url)
                        }
                    }, { position, item ->
                        showTextLongClickDialog(position, item)
                        true
                    }),
                    TextMsgSenderViewBinder({ _, item ->
                        val url = item.text?.content ?: ""
                        if (url.isNotBlank() && RegexUtils.isURL(url)) {
                            mLoginService?.goInnerWebBrowser(fragmentActivity, url)
                        }
                    }, { position, item ->
                        showTextLongClickDialog(position, item)
                        true
                    }),
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
                    ),
                    //语音
                    VoiceMsgViewSenderBinder {
                        clickVoice(it)
                    },
                    VoiceMsgViewReceiverBinder {
                        clickVoice(it)
                    }
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
                        ChatMsgType.VOICE.code -> {
                            if (isMe) VoiceMsgViewSenderBinder::class.java else VoiceMsgViewReceiverBinder::class.java
                        }
                        else -> {
                            VersionTooLowViewBinder::class.java
                        }
                    }
                }
        }
    }

    companion object {
        /**
         * 咨询室语音最大录制时间
         */
        const val MAX_VOICE_RECORD_DURATION = (60 * 1000).toLong()

        fun newInstance(args: Bundle? = Bundle()): ConversationChatFragment {
            val fragment = ConversationChatFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mVoiceRecordHandler.removeCallbacksAndMessages(null)
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
            itemAnimator = null
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
        //配置语音相关
        setVoiceTouchButton()
        setupVoicePlayer()
        vTakePhoto.click {
            val userId = mLoginService?.getUserId() ?: ""
            if (userId.isBlank()) {
                return@click
            }
            //拍照
            mRxTakePhoto.takeImageByCamera(fragmentActivity, false)
                .filter {
                    !it.isTakeCancel
                }
                .map {
                    it.imgPaths[0]
                }
                .flatMap {
                    //上传图片
                    mUploadPresenter.uploadImage(it)
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
            //最大选择数量
            val count = AppConstant.Config.MAX_IMAGE_COUNT
            mRxTakePhoto.takeImageByGallery(fragmentActivity, count, false)
                .filter {
                    !it.isTakeCancel
                }
                .flatMap {
                    //上传图片
                    mUploadPresenter.uploadMultipleImage(it.imgPaths)
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

    /**
     * 配置语音录制按钮
     */
    private fun setVoiceTouchButton() {
        //录音按钮事件绑定
        vChatInputBar.setVoiceButtonTouchCallback(object : VoiceRecordButton.ButtonTouchCallback {
            override fun isIntercept(): Boolean {
                val permission = Manifest.permission.RECORD_AUDIO
                val isAcceptPermission = mRxPermissions.isGranted(permission)
                if (!isAcceptPermission) {
                    //申请权限
                    mRxPermissions.request(permission)
                        .lifecycle(lifecycleOwner)
                        .subscribe {
                            if (it) {
                                LogUtils.d("获取录音权限成功")
                            } else {
                                toast("请允许录音权限，才能进行录制音频")
                            }
                        }
                }
                return !isAcceptPermission
            }

            override fun onStart() {
                mVoiceRecordHandler.removeCallbacksAndMessages(null)
                //如果正在播放音频，先停止
                val observable = if (mPlayerManager.isPrepared || mPlayerManager.isPlaying) {
                    mPlayerManager.stop()
                } else {
                    Observable.just(true)
                }
                observable.flatMap {
                    mRecorderManager.startRecord()
                        .doOnSubscribeUi {
                            //开始录制时，震动一下
                            VibratorHelper.startVibrator()
                            LogUtils.d("TouchCallback 录制开始...")
                            mVoiceRecordDialog.notifyStartRecord()
                        }
                }.ioToMain().lifecycle(lifecycleOwner)
                    .subscribe(RxUtil.nothingObserver())
            }

            override fun onCancel() {
                mRecorderManager.cancelRecord()
                    .doOnSubscribeUi {
                        LogUtils.d("TouchCallback 录制取消...")
                        mVoiceRecordDialog.notifyCancelRecord()
                    }.lifecycle(lifecycleOwner)
                    .subscribe(RxUtil.nothingObserver())
            }

            override fun onFinish() {
                mRecorderManager.finishRecord()
                    //由于结束录制的速度过快，可能会出现录制不完整的情况，所以延时400毫秒再结束
                    .delay(400, TimeUnit.MILLISECONDS)
                    .doOnSubscribeUi {
                        LogUtils.d("TouchCallback 录制结束...")
                        mVoiceRecordDialog.notifyFinishRecord()
                    }.lifecycle(lifecycleOwner)
                    .subscribe(RxUtil.nothingObserver())
            }

            override fun onTerminate() {
                mVoiceRecordHandler.postDelayed({
                    mVoiceRecordDialog.dismissDialog()
                }, 500L)
            }

            override fun onTouchIntervalTimeSmall() {
                mRecorderManager.cancelRecord()
                    .doOnSubscribeUi {
                        LogUtils.d("TouchCallback 录制时间太短...")
                        mVoiceRecordDialog.notifyTouchIntervalTimeSmall()
                    }.lifecycle(lifecycleOwner)
                    .subscribe(RxUtil.nothingObserver())
            }

            override fun onTouchCancelArea() {
                LogUtils.d("TouchCallback 手指移动到取消区域...")
                mVoiceRecordDialog.notifyTouchCancelArea()
            }

            override fun onRestoreNormalTouchArea() {
                LogUtils.d("TouchCallback 手指移动到正常区域...")
                mVoiceRecordDialog.notifyRestoreNormalTouchArea()
            }
        })
        //配置录音管理器
        mRecorderManager.applyMediaOption(
            RecorderOption
                .Builder()
                .setMaxDuration(MAX_VOICE_RECORD_DURATION)
                .setDebug(true)
                .build()
        )
        mRecorderManager.subscribeMediaRecorder()
            .lifecycle(lifecycleOwner)
            .subscribe(object : MediaRecorderObserver() {
                override fun onPrepared() {
                    super.onPrepared()
                    LogUtils.d("RxMediaRecorderManager 准备录制")
                }

                override fun onRecording() {
                    super.onRecording()
                    LogUtils.d("RxMediaRecorderManager 正在录制...")
                }

                override fun onFinish(
                    voiceId: String?,
                    audioFilePath: String?,
                    audioDuration: Int,
                    isCancel: Boolean
                ) {
                    super.onFinish(voiceId, audioFilePath, audioDuration, isCancel)
                    LogUtils.d("RxMediaRecorderManager 结束录制 文件录制: $audioFilePath 时长: $audioDuration")
                    //因为如果限制了最大时间，自动结束是，RecorderManager自动调用的，这里的就要Dialog强制关闭
                    if (mVoiceRecordDialog.isShowing) {
                        mVoiceRecordDialog.notifyFinishRecord()
                    }
                    if (isCancel || audioFilePath.isNullOrBlank()) {
                        return
                    }
                    //结束录音，发送录音消息
                    sendVoiceMsg(audioFilePath, audioDuration)
                }

                override fun onError(throwable: Throwable) {
                    super.onError(throwable)
                    //录音发生异常
                    mVoiceRecordDialog.notifyRecordError()
                }
            })
    }

    /**
     * 设置语音相关
     */
    private fun setupVoicePlayer() {
        mPlayerManager.subscribeMediaPlayer()
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe(object : MediaPlayObserver() {
                /**
                 * 更新语音条目
                 */
                private fun updatePlayItem(option: MediaOption, isPlay: Boolean) {
                    val playingUrl = mPlayHelper.urlVoiceHandler.getPlayingDataSource(option)
                    val model = findVoiceListModel(playingUrl)
                    model?.let {
                        //设置状态
                        it.data.isPlayingVoice = isPlay
                        mListAdapter.notifyItemChanged(it.index)
                    }
                }

                /**
                 * 根据url，查找它的item
                 */
                private fun findVoiceListModel(playUrl: String): DataIndexModel<ChatRecord>? {
                    val list = mListItems.filterIsInstance<ChatRecord>()
                        .filter {
                            it.type == ChatMsgType.VOICE.code &&
                                    it.voice != null && !TextUtils.isEmpty(it.voice?.mediaSrc)
                        }.filter {
                            playUrl == ApiUrl.getFullFileUrl(it.voice!!.mediaSrc)
                        }.map {
                            DataIndexModel<ChatRecord>(it, mListItems.indexOf(it))
                        }
                    return if (list.isEmpty()) null else list[0]
                }

                override fun onPrepared(applyMediaOption: MediaOption) {
                    super.onPrepared(applyMediaOption)
                    updatePlayItem(applyMediaOption, true)
                }

                override fun onPlaying(applyMediaOption: MediaOption) {
                    super.onPlaying(applyMediaOption)
                    updatePlayItem(applyMediaOption, true)
                }

                override fun onPause(applyMediaOption: MediaOption) {
                    super.onPause(applyMediaOption)
                    updatePlayItem(applyMediaOption, false)
                }

                override fun onStopped(applyMediaOption: MediaOption) {
                    super.onStopped(applyMediaOption)
                    updatePlayItem(applyMediaOption, false)
                }

                override fun onCompletion(applyMediaOption: MediaOption) {
                    super.onCompletion(applyMediaOption)
                    updatePlayItem(applyMediaOption, false)
                }
            })
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
                                        //没有更多了
                                        vRefreshLayout.setEnableRefresh(false)
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

            override fun onReceiveVoiceMsg(chatRecord: ChatRecord) {
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
     * 发送语音消息
     */
    private fun sendVoiceMsg(audioFilePath: String, audioDuration: Int) {
        val userId = mLoginService?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        Observable.just(true)
            .doOnSubscribeUi {
                mWaitController.showWait()
            }
            //上传音频
            .flatMap {
                mUploadPresenter.uploadFile(audioFilePath)
            }
            //发送音频消息
            .flatMap { url ->
                WebSocketAgent.getRxWebSocket(fragmentActivity)
                    .flatMap {
                        mConversationPresenter.sendVoiceMsg(
                            it,
                            mWsUrl,
                            userId,
                            mFriendUserId,
                            url,
                            audioDuration
                        )
                    }
            }
            .lifecycle(lifecycleOwner)
            .subscribe({
                mWaitController.hideWait()
            }, {
                it.printStackTrace()
                mWaitController.hideWait()
                toast(R.string.conversation_send_fail)
            })
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
        if (!mRecyclerViewScrollHelper.isScrollToBottom) {
            scrollToBottom()
        }
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
        val currentImageUrl = ApiUrl.getFullFileUrl(item.image!!.image)
        //收集所有图片Url
        val imageUrls = mListItems.filterIsInstance<ChatRecord>()
            .filter {
                it.type == ChatMsgType.IMAGE.code && it.image != null
            }.map {
                ApiUrl.getFullFileUrl(it.image!!.image)
            }.toList()
            //因为列表是倒着排的，所以这里需要倒序一下
            .reversed()
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

    /**
     * 点击语音条目
     */
    private fun clickVoice(item: ChatRecord) {
        //停止所有条目动画
        mListItems.filterIsInstance<ChatRecord>()
            .map {
                it.isPlayingVoice = false
            }
        mListAdapter.notifyDataSetChanged()
        //再执行播放操作，不然会出现多个播放动画同时出现的问题
        item.voice?.mediaSrc?.let {
            mPlayHelper.urlVoiceHandler.clickVoice(fragmentActivity, ApiUrl.getFullFileUrl(it))
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe(RxUtil.nothingObserver())
        }
    }
}