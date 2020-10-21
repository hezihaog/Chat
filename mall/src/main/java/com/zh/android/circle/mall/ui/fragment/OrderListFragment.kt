package com.zh.android.circle.mall.ui.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linghit.base.util.argument.bindArgument
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.handlerErrorCode
import com.zh.android.base.ext.ioToMain
import com.zh.android.base.ext.lifecycle
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.enums.OrderStatus
import com.zh.android.circle.mall.http.MallPresenter
import com.zh.android.circle.mall.item.OrderListViewBinder
import com.zh.android.circle.mall.model.OrderListModel
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/21
 * 订单列表
 */
class OrderListFragment : BaseFragment() {
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    /**
     * 订单状态
     */
    private val mOrderStatus by bindArgument(
        AppConstant.Key.MALL_ORDER_STATUS,
        OrderStatus.DEFAULT
    )

    /**
     * 当前页码
     */
    private var mCurrentPage: Int = ApiUrl.FIRST_PAGE

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(OrderListModel::class.java, OrderListViewBinder())
        }
    }

    private val mMallPresenter by lazy {
        MallPresenter()
    }

    override fun onInflaterViewId(): Int {
        return R.layout.base_refresh_layout
    }

    override fun onBindView(view: View?) {
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
        orderList(mOrderStatus, mCurrentPage)
    }

    private fun loadMore() {
        val nextPage = mCurrentPage + 1
        orderList(mOrderStatus, nextPage)
    }

    /**
     * 获取我的订单列表
     * @param orderStatus 订单状态
     */
    private fun orderList(
        orderStatus: OrderStatus,
        pageNum: Int
    ) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        val isFirstPage = pageNum == ApiUrl.FIRST_PAGE
        val pageSize = ApiUrl.PAGE_SIZE
        mMallPresenter.orderList(userId, orderStatus, pageNum, pageSize)
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
                                //每次刷新都滚动回顶部
                                vRefreshList.scrollToPosition(0)
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