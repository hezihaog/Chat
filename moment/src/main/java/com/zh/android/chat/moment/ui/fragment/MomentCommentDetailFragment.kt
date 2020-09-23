package com.zh.android.chat.moment.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linghit.base.util.argument.bindArgument
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
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
                    resources.getDimensionPixelSize(R.dimen.base_dimen_10)
                )
            )
            //评论的回复或回复的回复条目
            register(MomentCommentReplyModel::class.java)
                .to(
                    CommentReplyViewBinder(),
                    ReplyReplyViewBinder()
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
        mMomentPresenter.getMomentCommentReplyList(mMomentCommentId)
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
                    }
                }
            }, {
                it.printStackTrace()
                vRefreshLayout.finishRefresh(false)
                showRequestError()
            })
    }
}