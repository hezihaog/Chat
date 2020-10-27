package com.zh.android.circle.mall.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.apkfuns.logutils.LogUtils
import com.blankj.utilcode.util.RegexUtils
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.web.BrowserActivity
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.mall.MallService
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.enums.GoodsGroupType
import com.zh.android.circle.mall.http.MallPresenter
import com.zh.android.circle.mall.item.MallBannerViewBinder
import com.zh.android.circle.mall.item.MallIndexGoodsGroupViewBinder
import com.zh.android.circle.mall.item.MallIndexNavViewBinder
import com.zh.android.circle.mall.model.MallBannerModel
import com.zh.android.circle.mall.model.MallIndexGoodsGroupModel
import com.zh.android.circle.mall.model.MallIndexInfoModel
import com.zh.android.circle.mall.model.MallIndexNavModel
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/16
 * 商城首页
 */
class MallMainFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.MALL_SERVICE)
    var mMallService: MallService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)
    private val vShoppingCar: View by bindView(R.id.shopping_car)
    private val vShoppingCarDot: TextView by bindView(R.id.shopping_car_dot)

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            //轮播图
            register(MallBannerModel::class.java, MallBannerViewBinder {
                val url = it.redirectUrl
                if (url.isNotBlank() && RegexUtils.isURL(url)) {
                    BrowserActivity.start(fragmentActivity, it.redirectUrl)
                }
            })
            //商品分类
            register(
                MallIndexNavModel::class.java,
                MallIndexNavViewBinder { position, _ ->
                    val navModel = mListItems.filterIsInstance<MallIndexNavModel>()[0]
                    //点击的是全部，跳转到商品分类
                    if (position == navModel.navs.lastIndex) {
                        mMallService?.goGoodsCategory(fragmentActivity)
                    }
                })
            //商品分组
            register(MallIndexGoodsGroupModel::class.java, MallIndexGoodsGroupViewBinder {
                mMallService?.goGoodsDetail(fragmentActivity, it.goodsId)
            })
        }
    }

    private val mMallPresenter by lazy {
        MallPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MallMainFragment {
            val fragment = MallMainFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        cartItemListCount()
    }

    override fun onInflaterViewId(): Int {
        return R.layout.mall_index_main_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.mall_module_name)
            addRightImageButton(R.drawable.base_more, R.id.topbar_item_more).click {
                //更多
                PopupMenu(fragmentActivity, it).apply {
                    menuInflater.inflate(R.menu.mall_main_menu, menu)
                    setOnMenuItemClickListener { menu ->
                        when (menu.itemId) {
                            R.id.mall_my_order -> {
                                //我的订单
                                mMallService?.goMyOrder(fragmentActivity)
                                true
                            }
                            R.id.mall_my_address -> {
                                //我的收货地址
                                mMallService?.goUserAddressManage(fragmentActivity)
                                true
                            }
                            else -> {
                                false
                            }
                        }
                    }
                    show()
                }
            }
            addRightImageButton(R.drawable.base_search_black, R.id.topbar_item_search).click {
                //搜索
                mMallService?.goGoodsSearch(fragmentActivity)
            }
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
        vShoppingCar.click {
            //跳转到购物车
            mMallService?.goShoppingCar(fragmentActivity)
        }
    }

    override fun setData() {
        super.setData()
        refresh()
    }

    private fun refresh() {
        getIndexList()
    }

    private fun getIndexList() {
        mMallPresenter.indexInfos()
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                vRefreshLayout.finishRefresh()
                if (handlerErrorCode(httpModel)) {
                    httpModel.data?.let {
                        mListItems.clear()
                        mListItems.addAll(generateItems(it))
                        mListAdapter.notifyDataSetChanged()
                    }
                }
            }, { error ->
                error.printStackTrace()
                vRefreshLayout.finishRefresh(false)
                showRequestError()
            })
    }

    /**
     * 生成列表条目
     */
    private fun generateItems(model: MallIndexInfoModel): Items {
        return Items().apply {
            //轮播图条目
            if (model.carousels.isNotEmpty()) {
                add(
                    MallBannerModel(
                        model.carousels
                    )
                )
            }
            //分类列表
            add(
                MallIndexNavModel(
                    model.navs
                )
            )
            //最热商品
            if (model.hotGoods.isNotEmpty()) {
                add(
                    MallIndexGoodsGroupModel(
                        GoodsGroupType.HOT,
                        getString(R.string.mall_goods_group_hot),
                        model.hotGoods
                    )
                )
            }
            //最新商品
            if (model.newGoods.isNotEmpty()) {
                add(
                    MallIndexGoodsGroupModel(
                        GoodsGroupType.NEW,
                        getString(R.string.mall_goods_group_new),
                        model.newGoods
                    )
                )
            }
            //推荐商品
            if (model.recommendGoods.isNotEmpty()) {
                add(
                    MallIndexGoodsGroupModel(
                        GoodsGroupType.RECOMMEND,
                        getString(R.string.mall_goods_group_recommend),
                        model.recommendGoods
                    )
                )
            }
        }
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
}