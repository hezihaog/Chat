package com.zh.android.circle.mall.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draggable.library.extension.ImageViewerHelper
import com.linghit.base.util.argument.bindArgument
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.ext.handlerErrorCode
import com.zh.android.base.ext.ioToMain
import com.zh.android.base.ext.lifecycle
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.AppConstant
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.http.MallPresenter
import com.zh.android.circle.mall.item.GoodsDetailViewBinder
import com.zh.android.circle.mall.item.GoodsWebDetailViewBinder
import com.zh.android.circle.mall.model.GoodsWebDetailModel
import com.zh.android.circle.mall.model.MallGoodsModel
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/19
 * 商品详情
 */
class MallGoodsDetailFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    /**
     * 商品Id
     */
    private val mGoodsId by bindArgument(AppConstant.Key.MALL_GOODS_ID, "")

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(MallGoodsModel::class.java, GoodsDetailViewBinder { list, position, _ ->
                //跳转到图片预览
                ImageViewerHelper.showImages(
                    fragmentActivity,
                    list, index = position
                )
            })
            register(GoodsWebDetailModel::class.java, GoodsWebDetailViewBinder())
        }
    }

    private val mMallPresenter by lazy {
        MallPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MallGoodsDetailFragment {
            val fragment = MallGoodsDetailFragment()
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
            setTitle(R.string.mall_goods_detail)
            addRightImageButton(R.drawable.base_more, R.id.topbar_item_more).click {
                //更多
            }
        }
        vRefreshLayout.apply {
            setEnableRefresh(false)
            setEnableLoadMore(false)
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
        getGoodsDetail()
    }

    private fun getGoodsDetail() {
        mMallPresenter.getGoodsDetail(mGoodsId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                vRefreshLayout.finishRefresh()
                if (handlerErrorCode(httpModel)) {
                    httpModel.data?.let {
                        mListItems.clear()
                        //头部信息
                        mListItems.add(it)
                        //Web网页信息
                        mListItems.add(GoodsWebDetailModel(it.goodsDetailContent))
                        mListAdapter.notifyDataSetChanged()
                    }
                }
            }, { error ->
                error.printStackTrace()
                vRefreshLayout.finishRefresh(false)
            })
    }
}