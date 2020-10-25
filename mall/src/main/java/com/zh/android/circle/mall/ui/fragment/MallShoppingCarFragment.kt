package com.zh.android.circle.mall.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.BroadcastRegistry
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.mall.MallService
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.http.MallPresenter
import com.zh.android.circle.mall.item.ShoppingCartItemViewBinder
import com.zh.android.circle.mall.model.ShoppingCartItemModel
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/20
 * 购物车
 */
class MallShoppingCarFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.MALL_SERVICE)
    var mMallService: MallService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)
    private val vSelectAll: View by bindView(R.id.select_all)
    private val vSelectAllSymbol: ImageView by bindView(R.id.select_all_symbol)
    private val vTotalPrice: TextView by bindView(R.id.total_price)
    private val vSettlement: View by bindView(R.id.settlement)

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(ShoppingCartItemModel::class.java, ShoppingCartItemViewBinder({
                //切换选中
                it.isSelect = !it.isSelect
                notifyDataSetChanged()
                renderBottomBar()
            }, { model, newValue ->
                //点击了减少
                updateCartItem(model.cartItemId, newValue)
            }, { model, newValue ->
                //点击了，增加
                updateCartItem(model.cartItemId, newValue)
            }, {
                //跳转到商品详情
                mMallService?.goGoodsDetail(fragmentActivity, it.goodsId)
            }, { model ->
                //长按，显示删除
                AlertDialog.Builder(fragmentActivity)
                    .setItems(arrayOf(getString(R.string.base_delete))) { _, _ ->
                        //删除购物项
                        deleteCartItem(model.cartItemId)
                    }
                    .create()
                    .show()
                true
            }))
        }
    }

    private val mWaitLoadingController by lazy {
        WaitLoadingController(fragmentActivity, lifecycleOwner)
    }
    private val mMallPresenter by lazy {
        MallPresenter()
    }

    companion object {
        /**
         * 是否默认选中全选
         */
        private const val IS_DEFAULT_SELECT_ALL = true

        fun newInstance(args: Bundle? = Bundle()): MallShoppingCarFragment {
            val fragment = MallShoppingCarFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //支付成功，刷新购物车列表
        BroadcastRegistry(lifecycleOwner)
            .register(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    refresh()
                }
            }, AppConstant.Action.MALL_PAY_SUCCESS)
    }

    override fun onInflaterViewId(): Int {
        return R.layout.mall_shopping_car_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.mall_shopping_car)
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
        //全选
        vSelectAll.click {
            //是否选中全部
            val isSelectAll = isSelectAll()
            //全选变全不选，全部选变全选
            val result = !isSelectAll
            mListItems.filterIsInstance<ShoppingCartItemModel>()
                .map {
                    //设置状态
                    it.isSelect = result
                }
            mListAdapter.notifyDataSetChanged()
            renderBottomBar()
        }
        //全选图标
        vSelectAllSymbol.apply {
            //如果是默认选中，则改变图标
            setImageResource(
                if (IS_DEFAULT_SELECT_ALL) {
                    R.drawable.base_select
                } else {
                    R.drawable.base_un_select
                }
            )
        }
        vSettlement.click {
            val cartItemIds = ArrayList(
                getAllSelectItemList().map {
                    it.cartItemId
                }.toList()
            )
            if (cartItemIds.isEmpty()) {
                toast(R.string.mall_please_select_goods)
                return@click
            }
            //去结算
            mMallService?.goCreateOrder(fragmentActivity, cartItemIds)
        }
    }

    override fun setData() {
        super.setData()
        refresh()
    }

    private fun refresh() {
        cartItemList()
    }

    /**
     * 获取购物车项列表
     */
    private fun cartItemList() {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mMallPresenter.cartItemList(userId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                vRefreshLayout.finishRefresh()
                if (handlerErrorCode(httpModel)) {
                    val list = (httpModel.data ?: mutableListOf()).map {
                        //如果是默认选中所有，则将选中标志都设置为true
                        if (IS_DEFAULT_SELECT_ALL) {
                            it.isSelect = true
                        }
                        it
                    }
                    mListItems.clear()
                    mListItems.addAll(list)
                    mListAdapter.notifyDataSetChanged()
                    //渲染底部栏
                    renderBottomBar()
                }
            }, { error ->
                error.printStackTrace()
                showRequestError()
                vRefreshLayout.finishRefresh(false)
            })
    }

    /**
     * 更新一个购物车项（增加、减少购买数量）
     */
    private fun updateCartItem(
        cartItemId: String,
        goodsCount: Int
    ) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mMallPresenter.updateCartItem(userId, cartItemId, goodsCount)
            .doOnSubscribeUi {
                mWaitLoadingController.showWait()
            }
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                mWaitLoadingController.hideWait()
                if (handlerErrorCode(httpModel)) {
                    //找到那个商品，更新购买数量
                    mListItems.filterIsInstance<ShoppingCartItemModel>()
                        .filter {
                            it.cartItemId == cartItemId
                        }.map {
                            it.goodsCount = goodsCount
                        }
                    mListAdapter.notifyDataSetChanged()
                    //重新渲染底部栏
                    renderBottomBar()
                }
            }, {
                it.printStackTrace()
                showRequestError()
                mWaitLoadingController.hideWait()
            })
    }

    /**
     * 删除一项购物车商品信息
     */
    private fun deleteCartItem(
        cartItemId: String
    ) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mMallPresenter.deleteCartItem(userId, cartItemId)
            .doOnSubscribeUi {
                mWaitLoadingController.showWait()
            }
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                mWaitLoadingController.hideWait()
                if (handlerErrorCode(httpModel)) {
                    //删除那一个购物项
                    val results = mListItems.filterIsInstance<ShoppingCartItemModel>()
                        .filter {
                            it.cartItemId == cartItemId
                        }
                    //一般不会找不到
                    if (results.isEmpty()) {
                        return@subscribe
                    }
                    //找到位置，再删除
                    val model = results[0]
                    val position = mListItems.indexOf(model)
                    mListItems.remove(model)
                    mListAdapter.fixNotifyItemRemoved(position)
                    //重新渲染底部栏
                    renderBottomBar()
                }
            }, {
                it.printStackTrace()
                showRequestError()
                mWaitLoadingController.hideWait()
            })
    }

    /**
     * 渲染底部栏
     */
    private fun renderBottomBar() {
        //选中的条目
        val selectModels = mListItems.filterIsInstance<ShoppingCartItemModel>()
            .filter {
                //只计算选中的项
                it.isSelect
            }
        //有选中的商品，就计算
        if (selectModels.isNotEmpty()) {
            //1、计算总价，并渲染
            val totalPrice = selectModels.map {
                //单价 x 数量 = 一种商品的价格
                it.sellingPrice * it.goodsCount
            }.reduce { sum, nextValue ->
                //每次累计，上一次的累计结果 + 本次的价格 = 总价
                sum + nextValue
            }
            vTotalPrice.text = totalPrice.toString()
        } else {
            //没有一个选中
            vTotalPrice.text = "0"
        }
        //2、根据全选状态，切换图标
        vSelectAllSymbol.apply {
            val selectAll = isSelectAll()
            setImageResource(
                if (selectAll) {
                    R.drawable.base_select
                } else {
                    R.drawable.base_un_select
                }
            )
        }
    }

    /**
     * 获取所有选中的条目
     */
    private fun getAllSelectItemList(): List<ShoppingCartItemModel> {
        return mListItems.filterIsInstance<ShoppingCartItemModel>()
            .filter {
                it.isSelect
            }
    }

    /**
     * 是否选中所有
     */
    private fun isSelectAll(): Boolean {
        //所有购物车项的数量
        val allShoppingCardItemCount = mListItems.filterIsInstance<ShoppingCartItemModel>().count()
        //选中的数量
        val selectCount = getAllSelectItemList().count()
        //他们的数量相同，则代表全选
        return allShoppingCardItemCount == selectCount
    }
}