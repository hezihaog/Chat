package com.zh.android.chat.moment.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apkfuns.logutils.LogUtils
import com.linghit.base.util.argument.bindArgument
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.enums.MomentReplyType
import com.zh.android.chat.moment.http.MomentPresenter
import com.zh.android.chat.moment.item.CommentReplyViewBinder
import com.zh.android.chat.moment.item.MomentCommentViewBinder
import com.zh.android.chat.moment.item.ReplyReplyViewBinder
import com.zh.android.chat.moment.model.MomentCommentModel
import com.zh.android.chat.moment.model.MomentCommentReplyModel
import com.zh.android.chat.moment.ui.widget.MomentInputBar
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/09/22
 * 动态详情
 */
class MomentCommentDetailFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)
    private val vMomentInputBar: MomentInputBar by bindView(R.id.moment_input_bar)

    private val mMomentCommentId by bindArgument(AppConstant.Key.MOMENT_COMMENT_ID, "")

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            //评论条目
            register(
                MomentCommentModel::class.java, MomentCommentViewBinder(
                    true,
                    clickCommentCallback = {
                        changeInputBarTarget(it)
                    },
                    dividerHeight = resources.getDimensionPixelSize(R.dimen.base_dimen_10),
                    clickDeleteCallback = { _, item ->
                        //删除评论
                        AlertDialog.Builder(fragmentActivity)
                            .setMessage(R.string.moment_confirm_delete)
                            .setPositiveButton(R.string.base_confirm) { _, _ ->
                                deleteMomentComment(item)
                            }
                            .setNegativeButton(R.string.base_cancel) { _, _ ->
                                LogUtils.d("取消删除")
                            }
                            .create()
                            .show()
                    }
                )
            )
            //评论的回复或回复的回复条目
            register(MomentCommentReplyModel::class.java)
                .to(
                    CommentReplyViewBinder(
                        clickCommentCallback = {
                            changeInputBarTarget(it)
                        },
                        clickDeleteCallback = {
                            //删除评论的回复，或者回复的回复
                            removeMomentCommentReply(it)
                        }
                    ),
                    ReplyReplyViewBinder(
                        clickCommentCallback = {
                            changeInputBarTarget(it)
                        },
                        clickDeleteCallback = {
                            //删除评论的回复，或者回复的回复
                            removeMomentCommentReply(it)
                        }
                    )
                )
                .withClassLinker { _, model ->
                    when (model.type) {
                        MomentReplyType.COMMENT_REPLY.code -> {
                            CommentReplyViewBinder::class.java
                        }
                        MomentReplyType.REPLY_REPLY.code -> {
                            ReplyReplyViewBinder::class.java
                        }
                        else -> {
                            CommentReplyViewBinder::class.java
                        }
                    }
                }
        }
    }

    private val mMomentPresenter by lazy {
        MomentPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MomentCommentDetailFragment {
            val fragment = MomentCommentDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.moment_comment_detail_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.moment_comment)
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
        vMomentInputBar.apply {
            //隐藏点赞和评论
            hideLikeView()
            hideCommentView()
        }
    }

    override fun setData() {
        super.setData()
        refresh()
    }

    private fun refresh() {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrEmpty()) {
            return
        }
        mMomentPresenter.getMomentCommentReplyList(mMomentCommentId, userId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                vRefreshLayout.finishRefresh()
                if (handlerErrorCode(httpModel)) {
                    httpModel.data?.let { commentModel ->
                        //取出回复列表
                        val replyList = mutableListOf<MomentCommentReplyModel>().apply {
                            addAll(commentModel.replyList)
                        }
                        //清空掉，这里需要将列表铺平展示
                        commentModel.replyList.clear()
                        mListItems.clear()
                        //添加动态评论
                        mListItems.addAll(listOf(commentModel))
                        //添加回复列表
                        mListItems.addAll(replyList)
                        mListAdapter.notifyDataSetChanged()
                        //默认选择回复评论
                        changeInputBarTarget(commentModel)
                    }
                }
            }, {
                it.printStackTrace()
                vRefreshLayout.finishRefresh(false)
                showRequestError()
            })
    }

    /**
     * 切换底部栏的回复目标，评论的回复
     */
    private fun changeInputBarTarget(model: MomentCommentModel) {
        vMomentInputBar.setInputHintText(
            resources.getString(R.string.moment_replay_to2, model.userInfo.nickname)
        )
        //设置回调
        vMomentInputBar.setOnActionCallback(object : MomentInputBar.OnActionCallbackAdapter() {
            override fun onClickSendBefore(input: String?): Boolean {
                return !input.isNullOrBlank()
            }

            override fun onClickSendAfter(inputText: String) {
                addMomentCommentReply(
                    model.id,
                    null,
                    model.userInfo.id,
                    inputText,
                    MomentReplyType.COMMENT_REPLY.code
                )
            }
        })
    }

    /**
     * 切换底部栏的回复目标，回复的回复
     */
    private fun changeInputBarTarget(model: MomentCommentReplyModel) {
        vMomentInputBar.setInputHintText(
            resources.getString(R.string.moment_replay_to2, model.userInfo.nickname)
        )
        //设置回调
        vMomentInputBar.setOnActionCallback(object : MomentInputBar.OnActionCallbackAdapter() {
            override fun onClickSendBefore(input: String?): Boolean {
                return !input.isNullOrBlank()
            }

            override fun onClickSendAfter(inputText: String) {
                addMomentCommentReply(
                    null,
                    model.id,
                    model.userInfo.id,
                    inputText,
                    MomentReplyType.REPLY_REPLY.code
                )
            }
        })
    }

    /**
     * 增加一条动态的评论的回复，或者回复的回复
     */
    private fun addMomentCommentReply(
        commentId: String?,
        parentId: String?,
        replyUserId: String,
        content: String,
        typeCode: Int
    ) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        val typeEnum = MomentReplyType.getByCode(typeCode) ?: return
        mMomentPresenter.addMomentCommentReply(
            commentId,
            parentId,
            userId,
            replyUserId,
            content,
            typeEnum
        ).ioToMain().lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    vMomentInputBar.setInputText("")
                    toast(R.string.moment_replay_success)
                    vRefreshLayout.autoRefresh()
                    //刷新外层列表
                    AppBroadcastManager.sendBroadcast(
                        AppConstant.Action.MOMENT_DETAIL_REFRESH
                    )
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }

    /**
     * 删除一条动态评论
     */
    private fun deleteMomentComment(model: MomentCommentModel) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrEmpty()) {
            return
        }
        mMomentPresenter.deleteMomentComment(
            model.id,
            model.momentId,
            userId
        ).ioToMain().lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    toast(R.string.base_delete_success)
                    //删除后，刷新外面的列表
                    AppBroadcastManager.sendBroadcast(
                        AppConstant.Action.MOMENT_DETAIL_REFRESH
                    )
                    fragmentActivity.finish()
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }

    /**
     * 删除一条动态的评论的回复，或者回复的回复
     */
    private fun removeMomentCommentReply(item: MomentCommentReplyModel) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrEmpty()) {
            return
        }
        fun deleteAction() {
            mMomentPresenter.removeMomentCommentReply(item.id, userId)
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({ httpModel ->
                    if (handlerErrorCode(httpModel)) {
                        val position = mListItems.indexOf(item)
                        if (position != -1) {
                            toast(R.string.base_delete_success)
                            mListItems.remove(item)
                            mListAdapter.fixNotifyItemRemoved(position)
                            AppBroadcastManager.sendBroadcast(
                                AppConstant.Action.MOMENT_DETAIL_REFRESH
                            )
                        }
                    }
                }, {
                    it.printStackTrace()
                    showRequestError()
                })
        }
        AlertDialog.Builder(fragmentActivity)
            .setMessage(R.string.moment_confirm_delete)
            .setPositiveButton(R.string.base_confirm) { _, _ ->
                deleteAction()
            }
            .setNegativeButton(R.string.base_cancel) { _, _ ->
                LogUtils.d("取消删除")
            }
            .create()
            .show()
    }
}