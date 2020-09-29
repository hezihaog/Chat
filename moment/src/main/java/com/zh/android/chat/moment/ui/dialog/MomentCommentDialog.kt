package com.zh.android.chat.moment.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apkfuns.logutils.LogUtils
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.*
import com.zh.android.base.ui.dialog.bottomsheet.BaseBottomSheetDialog
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.http.MomentPresenter
import com.zh.android.chat.moment.item.MomentCommentViewBinder
import com.zh.android.chat.moment.model.MomentCommentModel
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.ext.getMomentService
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/09/29
 * 动态评论弹窗
 */
class MomentCommentDialog(context: Context, owner: LifecycleOwner) :
    BaseBottomSheetDialog(context, owner) {

    private lateinit var vRefreshLayout: SmartRefreshLayout
    private lateinit var vRefreshList: RecyclerView

    /**
     * 动态Id
     */
    private lateinit var mMomentId: String

    private var mCurrentPage: Int = ApiUrl.FIRST_PAGE

    private lateinit var mListItems: Items
    private lateinit var mListAdapter: MultiTypeAdapter

    private val mMomentPresenter by lazy {
        MomentPresenter()
    }

    override fun onCreateContentView(inflater: LayoutInflater?, parent: ViewGroup?): View {
        return inflater!!.inflate(R.layout.moment_comment_dialog, parent, false)
    }

    override fun onBindView(view: View?) {
        super.onBindView(view)
        view?.let {
            findView(it)
            bindView()
        }
    }

    private fun findView(view: View) {
        vRefreshLayout = view.findViewById(R.id.base_refresh_layout)
        vRefreshList = view.findViewById(R.id.base_refresh_list)
    }

    private fun bindView() {
        vRefreshLayout.apply {
            setEnableRefresh(false)
            setOnLoadMoreListener {
                loadMore()
            }
        }
        vRefreshList.apply {
            mListItems = Items()
            mListAdapter = MultiTypeAdapter(mListItems).apply {
                register(MomentCommentModel::class.java, MomentCommentViewBinder(
                    dividerHeight = activity.resources.getDimensionPixelSize(R.dimen.base_dimen_zero_point_five),
                    clickDeleteCallback = { position, item ->
                        //删除评论
                        AlertDialog.Builder(activity)
                            .setMessage(R.string.moment_confirm_delete)
                            .setPositiveButton(R.string.base_confirm) { _, _ ->
                                deleteMomentComment(position, item)
                            }
                            .setNegativeButton(R.string.base_cancel) { _, _ ->
                                LogUtils.d("取消删除")
                            }
                            .create()
                            .show()
                    },
                    clickItemCallback = {
                        //跳转评论详情
                        getMomentService()?.goMomentCommentDetail(
                            activity,
                            it.momentId,
                            it.id
                        )
                    }
                ))
            }
            layoutManager = LinearLayoutManager(activity)
            adapter = mListAdapter
        }
    }

    /**
     * 提供给外面调用加载数据使用
     * @param momentId 动态Id
     */
    fun loadData(momentId: String): MomentCommentDialog {
        this.mMomentId = momentId
        refresh()
        return this
    }

    private fun refresh() {
        mCurrentPage = ApiUrl.FIRST_PAGE
        getMomentCommentList(mMomentId, mCurrentPage)
    }

    private fun loadMore() {
        val nextPage = mCurrentPage + 1
        getMomentCommentList(mMomentId, nextPage)
    }

    /**
     * 获取动态点赞列表
     */
    private fun getMomentCommentList(
        momentId: String,
        pageNum: Int
    ) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrEmpty()) {
            return
        }
        val isFirstPage = pageNum == ApiUrl.FIRST_PAGE
        val pageSize = ApiUrl.PAGE_SIZE
        mMomentPresenter.getMomentCommentList(momentId, userId, pageNum, pageSize)
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
                                vRefreshLayout.finishLoadMoreWithNoMoreData()
                            }
                        } else {
                            mListItems.apply {
                                if (isFirstPage) {
                                    mListItems.clear()
                                }
                                addAll(resultList)
                            }
                            mListAdapter.notifyDataSetChanged()
                            //最后一页
                            if (resultList.size < pageSize) {
                                vRefreshLayout.finishRefresh()
                                vRefreshLayout.finishLoadMoreWithNoMoreData()
                            } else {
                                if (isFirstPage) {
                                    vRefreshLayout.finishRefresh()
                                } else {
                                    mCurrentPage++
                                    vRefreshLayout.finishLoadMore()
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
                    vRefreshLayout.finishLoadMore(false)
                }
            }, { error ->
                error.printStackTrace()
                if (isFirstPage) {
                    vRefreshLayout.finishRefresh(false)
                } else {
                    vRefreshLayout.finishLoadMore(false)
                }
            })
    }

    /**
     * 删除一条动态评论
     */
    private fun deleteMomentComment(position: Int, model: MomentCommentModel) {
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
                    com.zh.android.base.ext.toast(R.string.base_delete_success)
                    mListItems.remove(model)
                    mListAdapter.fixNotifyItemRemoved(position)
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }
}