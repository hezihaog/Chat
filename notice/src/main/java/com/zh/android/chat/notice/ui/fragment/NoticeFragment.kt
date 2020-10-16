package com.zh.android.chat.notice.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.RegexUtils
import com.linghit.base.util.argument.bindArgument
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.web.BrowserActivity
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.notice.R
import com.zh.android.chat.notice.http.NoticePresenter
import com.zh.android.chat.notice.item.NoticeItemViewBinder
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.notice.model.NoticeModel
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/06
 * 通知
 */
class NoticeFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    /**
     * 通知Id
     */
    private var mNoticeId: String by bindArgument(AppConstant.Key.NOTICE_ID, "")

    /**
     * 通知Detail
     */
    private var mNoticeDetail: String by bindArgument(AppConstant.Key.NOTICE_DETAIL, "")

    private var mCurrentPage: Int = ApiUrl.FIRST_PAGE

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(NoticeModel::class.java, NoticeItemViewBinder { position, item ->
                //已读
                readNotice(position, item.id)
                //跳转到Web页面
                item.detail?.let {
                    goWebBrowse(it)
                }
            })
        }
    }

    private val mNoticePresenter by lazy {
        NoticePresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): NoticeFragment {
            val fragment = NoticeFragment()
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
            setTitle(R.string.notice_module_name)
            addRightTextButton(R.string.notice_read_all, R.id.notice_read_all).click {
                AlertDialog.Builder(fragmentActivity)
                    .setMessage(R.string.notice_read_all_tip)
                    .setPositiveButton(R.string.base_confirm) { _, _ ->
                        //全部已读
                        readAllNotice()
                    }
                    .setNegativeButton(R.string.base_cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
        vRefreshLayout.apply {
            setOnRefreshListener {
                refresh()
            }
            setOnLoadMoreListener {
                loadMore()
            }
        }
        vRefreshList.apply {
            layoutManager = LinearLayoutManager(fragmentActivity)
            adapter = mListAdapter
        }
    }

    override fun setData() {
        super.setData()
        //如果前面传了通知Id，直接已读
        if (mNoticeId.isNotBlank()) {
            readNotice(-1, mNoticeId)
        }
        //如果前面传了通知Detail，直接跳转
        if (mNoticeDetail.isNotBlank()) {
            goWebBrowse(mNoticeDetail)
        }
        vRefreshLayout.autoRefresh()
    }

    fun onNewIntent(intent: Intent?) {
        intent?.run {
            mNoticeId = getStringExtra(AppConstant.Key.NOTICE_ID) ?: ""
            mNoticeDetail = getStringExtra(AppConstant.Key.NOTICE_DETAIL) ?: ""
        }
        setData()
    }

    private fun refresh() {
        mCurrentPage = ApiUrl.FIRST_PAGE
        getNoticeList(mCurrentPage)
    }

    private fun loadMore() {
        val nextPage = mCurrentPage + 1
        getNoticeList(nextPage)
    }

    /**
     * 获取通知列表
     */
    private fun getNoticeList(
        pageNum: Int
    ) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrEmpty()) {
            return
        }
        val isFirstPage = pageNum == ApiUrl.FIRST_PAGE
        val pageSize = ApiUrl.PAGE_SIZE
        mNoticePresenter.getNoticeList(userId, pageNum, pageSize)
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
     * 已读一条通知
     */
    private fun readNotice(position: Int, noticeId: String) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mNoticePresenter.readNotice(noticeId, userId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    if (position != -1) {
                        val model = mListItems[position]
                        if (model is NoticeModel) {
                            model.read = true
                        }
                        mListAdapter.notifyDataSetChanged()
                    }
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }

    /**
     * 已读所有通知
     */
    private fun readAllNotice() {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mNoticePresenter.readAllNotice(userId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    toast(R.string.notice_read_all_success)
                    mListItems.filterIsInstance<NoticeModel>().map {
                        it.read = true
                    }
                    mListAdapter.notifyDataSetChanged()
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }

    /**
     * 跳转到Web页面
     */
    private fun goWebBrowse(url: String) {
        if (url.isNotBlank() && RegexUtils.isURL(url)) {
            BrowserActivity.start(fragmentActivity, url)
        }
    }
}