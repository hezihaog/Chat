package com.zh.android.circle.mall.ui.fragment

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
import com.zh.android.base.ext.*
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.module.mall.MallService
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.enums.OrderByType
import com.zh.android.circle.mall.http.MallPresenter
import com.zh.android.circle.mall.item.GoodsViewBinder
import com.zh.android.circle.mall.model.MallGoodsModel
import com.zh.android.circle.mall.ui.widget.GoodsSortSegment
import com.zh.android.circle.mall.ui.widget.MallSearchBar2
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/19
 * 商品搜索
 */
class MallGoodsSearchFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.MALL_SERVICE)
    var mMallService: MallService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)
    private val vSearchBar: MallSearchBar2 by lazy {
        MallSearchBar2(fragmentActivity)
    }
    private val vSortSegment: GoodsSortSegment by bindView(R.id.sort_segment)

    /**
     * 商品分类Id
     */
    private val mGoodsCategoryId: String by bindArgument(AppConstant.Key.MALL_GOODS_CATEGORY_ID, "")

    /**
     * 当前页码
     */
    private var mCurrentPage: Int = ApiUrl.FIRST_PAGE

    /**
     * 当前的关键字
     */
    private var mCurrentKeyword: String = ""

    /**
     * 当前的排序规则
     */
    private var mCurrentOrderByType: OrderByType = OrderByType.DEFAULT

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(MallGoodsModel::class.java, GoodsViewBinder {
                //跳转商品详情
                mMallService?.goGoodsDetail(fragmentActivity, it.goodsId)
            })
        }
    }

    private val mMallPresenter by lazy {
        MallPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MallGoodsSearchFragment {
            val fragment = MallGoodsSearchFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.mall_goods_search_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            addRightTextButton(R.string.base_search, R.id.topbar_item_search).click {
                val inputText = vSearchBar.inputText
                if (inputText.isBlank()) {
                    toast(R.string.base_search_hint)
                    return@click
                }
                //关键字搜索
                mCurrentKeyword = inputText
                //必传一个参数，不能2个都不传
                if (mCurrentKeyword.isBlank() && mGoodsCategoryId.isBlank()) {
                    return@click
                }
                vRefreshLayout.autoRefresh()
            }
            centerView = vSearchBar
        }
        vSearchBar.apply {
            setCallback {
                mCurrentKeyword = vSearchBar.inputText
                refresh()
            }
        }
        vSortSegment.apply {
            setCallback {
                //切换排序规则，刷新
                mCurrentOrderByType = it
                //必传一个参数，不能2个都不传
                if (mCurrentKeyword.isBlank() && mGoodsCategoryId.isBlank()) {
                    return@setCallback
                }
                vRefreshLayout.autoRefresh()
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
        //如果前面传递过来了分组Id，则先查一次
        if (mGoodsCategoryId.isNotBlank()) {
            refresh()
        }
    }

    private fun refresh() {
        mCurrentPage = ApiUrl.FIRST_PAGE
        search(mCurrentKeyword, mGoodsCategoryId, mCurrentOrderByType, mCurrentPage)
    }

    private fun loadMore() {
        val nextPage = mCurrentPage + 1
        search(mCurrentKeyword, mGoodsCategoryId, mCurrentOrderByType, nextPage)
    }

    /**
     * 搜索
     * @param keyword 关键字
     * @param goodsCategoryId 分类Id
     */
    private fun search(
        keyword: String,
        goodsCategoryId: String,
        orderBy: OrderByType,
        pageNum: Int
    ) {
        val isFirstPage = pageNum == ApiUrl.FIRST_PAGE
        val pageSize = ApiUrl.PAGE_SIZE
        mMallPresenter.searchGoods(keyword, goodsCategoryId, orderBy, pageNum, pageSize)
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