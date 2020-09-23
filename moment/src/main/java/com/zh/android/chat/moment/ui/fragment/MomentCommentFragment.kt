package com.zh.android.chat.moment.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.linghit.base.util.argument.bindArgument
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.handlerErrorCode
import com.zh.android.base.ext.ioToMain
import com.zh.android.base.ext.lifecycle
import com.zh.android.base.util.BroadcastRegistry
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.http.MomentPresenter
import com.zh.android.chat.moment.item.MomentCommentViewBinder
import com.zh.android.chat.moment.model.MomentCommentModel
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.module.moment.MomentService
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/09/19
 * 动态评论列表
 */
class MomentCommentFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.MOMENT_SERVICE)
    var mMomentService: MomentService? = null

    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    private val mMomentId by bindArgument(AppConstant.Key.MOMENT_ID, "")

    private var mCurrentPage: Int = ApiUrl.FIRST_PAGE

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(MomentCommentModel::class.java, MomentCommentViewBinder(
                dividerHeight = resources.getDimensionPixelSize(R.dimen.base_dimen_zero_point_five)
            ) {
                mMomentService?.goMomentCommentDetail(
                    fragmentActivity,
                    it.momentId,
                    it.id
                )
            })
        }
    }

    private val mMomentPresenter by lazy {
        MomentPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BroadcastRegistry(fragment)
            .register(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    refresh()
                }
            }, AppConstant.Action.MOMENT_ADD_COMMENT_SUCCESS)
    }

    override fun onInflaterViewId(): Int {
        return R.layout.base_refresh_layout
    }

    override fun onBindView(view: View?) {
        vRefreshLayout.apply {
            setEnableRefresh(false)
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
        refresh()
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
        val isFirstPage = pageNum == ApiUrl.FIRST_PAGE
        val pageSize = ApiUrl.PAGE_SIZE
        mMomentPresenter.getMomentCommentList(momentId, pageNum, pageSize)
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
}