package com.zh.android.circle.mall.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.apkfuns.logutils.LogUtils
import com.draggable.library.extension.ImageViewerHelper
import com.linghit.base.util.argument.bindArgument
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.mall.MallService
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
    @JvmField
    @Autowired(name = ARouterUrl.MALL_SERVICE)
    var mMallService: MallService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)
    private val vCustomerService: View by bindView(R.id.customer_service)
    private val vShoppingCar: View by bindView(R.id.shopping_car)
    private val vShoppingCarDot: TextView by bindView(R.id.shopping_car_dot)
    private val vAddToShoppingCard: View by bindView(R.id.add_to_shopping_card)

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
                    list.map {
                        ApiUrl.getFullFileUrl(it)
                    }, index = position
                )
            })
            register(GoodsWebDetailModel::class.java, GoodsWebDetailViewBinder(fragmentActivity))
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
        return R.layout.mall_goods_detail_fragment
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
        //客服
        vCustomerService.click {
            toast("客服中心")
        }
        vShoppingCar.click {
            //跳转到购物车
            mMallService?.goShoppingCar(fragmentActivity)
        }
        vAddToShoppingCard.click {
            //将当前商品，添加到购物车
            saveShoppingCartItem(mGoodsId, 1)
        }
    }

    override fun onResume() {
        super.onResume()
        cartItemListCount()
    }

    override fun setData() {
        super.setData()
        refresh()
    }

    private fun refresh() {
        getGoodsDetail()
    }

    /**
     * 获取商品详情
     */
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
                showRequestError()
                vRefreshLayout.finishRefresh(false)
            })
    }

    /**
     * 获取购物车列表的数量
     */
    private fun cartItemListCount() {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mMallPresenter.cartItemListCount(userId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({
                if (checkHttpResponse(it)) {
                    val count = it.data ?: 0
                    vShoppingCarDot.apply {
                        if (count > 0) {
                            setVisible()
                            text = count.toString()
                        } else {
                            setGone()
                        }
                    }
                }
            }, {
                it.printStackTrace()
                LogUtils.d("获取购物车列表数量失败")
            })
    }

    /**
     * 保存商品到购物车
     */
    private fun saveShoppingCartItem(
        goodsId: String,
        goodsCount: Int
    ) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mMallPresenter.saveShoppingCartItem(userId, goodsId, goodsCount)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    //添加成功，刷新一下购物车数量
                    cartItemListCount()
                    toast(R.string.mall_add_to_shopping_car_success)
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }
}