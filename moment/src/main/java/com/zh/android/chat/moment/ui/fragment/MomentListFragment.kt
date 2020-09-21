package com.zh.android.chat.moment.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.ShareUtil
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.http.MomentPresenter
import com.zh.android.chat.moment.item.MomentItemViewBinder
import com.zh.android.chat.moment.model.MomentModel
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.moment.MomentService
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/09/19
 * 动态列表
 */
class MomentListFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.MOMENT_SERVICE)
    var mMomentService: MomentService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    private var mCurrentPage: Int = ApiUrl.FIRST_PAGE

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(MomentModel::class.java, MomentItemViewBinder(
                { _, item ->
                    //取反状态
                    val isLike = !item.liked
                    likeOrRemoveLikeMoment(isLike, item.id)
                }, { _, item ->
                    mMomentService?.goMomentDetail(fragmentActivity, item.id)
                }, { _, item ->
                    ShareUtil.shareText(fragmentActivity, item.content)
                }, { _, item ->
                    mMomentService?.goMomentDetail(fragmentActivity, item.id)
                }
            ))
        }
    }

    private val mMomentPresenter by lazy {
        MomentPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MomentListFragment {
            val fragment = MomentListFragment()
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
            setTitle(R.string.moment_module_name)
            addRightImageButton(R.drawable.moment_publish, R.id.moment_publish).click {
                mMomentService?.goMomentPublish(fragmentActivity)
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
        refresh()
    }

    private fun refresh() {
        mCurrentPage = ApiUrl.FIRST_PAGE
        getMomentList(mCurrentPage)
    }

    private fun loadMore() {
        val nextPage = mCurrentPage + 1
        getMomentList(nextPage)
    }

    /**
     * 点赞或取消点赞，动态
     */
    private fun likeOrRemoveLikeMoment(isLike: Boolean, momentId: String) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        val observable = if (isLike) {
            mMomentPresenter.likeMoment(momentId, userId)
        } else {
            mMomentPresenter.removeLikeMoment(momentId, userId)
        }
        observable.ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    httpModel?.data?.let { response ->
                        //更新数据
                        mListItems
                            //只取一个动态类型
                            .filterIsInstance<MomentModel>()
                            //只修改自己那一个动态
                            .filter {
                                it.id == response.momentId
                            }
                            .forEach {
                                it.liked = response.liked
                                it.likes = response.likes
                            }
                    }
                    mListAdapter.notifyDataSetChanged()
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }

    /**
     * 获取动态列表
     */
    private fun getMomentList(
        pageNum: Int
    ) {
        val userId = getLoginService()?.getUserId()
        val isFirstPage = pageNum == ApiUrl.FIRST_PAGE
        val pageSize = ApiUrl.PAGE_SIZE
        mMomentPresenter.getMomentList(
            userId, pageNum, ApiUrl.PAGE_SIZE
        ).ioToMain()
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