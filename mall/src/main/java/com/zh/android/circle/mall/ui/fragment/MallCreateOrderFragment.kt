package com.zh.android.circle.mall.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.linghit.base.util.argument.bindArgument
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.mall.MallService
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.enums.PayType
import com.zh.android.circle.mall.http.MallPresenter
import com.zh.android.circle.mall.item.ChooseUserAddressViewBinder
import com.zh.android.circle.mall.item.OrderItemViewBinder
import com.zh.android.circle.mall.model.ChooseUserAddressModel
import com.zh.android.circle.mall.model.ShoppingCartItemModel
import com.zh.android.circle.mall.model.UserAddressModel
import com.zh.android.circle.mall.ui.dialog.MallPayWayDialog
import io.reactivex.Observable
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import java.util.*

/**
 * @author wally
 * @date 2020/10/24
 * 创建订单
 */
class MallCreateOrderFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.MALL_SERVICE)
    var mMallService: MallService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)
    private val vTotalPrice: TextView by bindView(R.id.total_price)
    private val vCreateOrder: TextView by bindView(R.id.create_order)

    /**
     * 用户选择的收货地址信息
     */
    private var mChooseUserAddressInfo = ChooseUserAddressModel()

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            //收货地址信息条目
            register(ChooseUserAddressModel::class.java, ChooseUserAddressViewBinder {
                //跳转去选择收货地址
                mMallService?.goChooseUserAddress(fragmentActivity)
            })
            //购物车项
            register(ShoppingCartItemModel::class.java, OrderItemViewBinder())
        }
    }

    private val mWaitLoadingController by lazy {
        WaitLoadingController(fragmentActivity, lifecycleOwner)
    }
    private val mMallPresenter by lazy {
        MallPresenter()
    }

    /**
     * 要准备计算的购物车项Id列表
     */
    private val mCartItemIds by bindArgument(
        AppConstant.Key.MALL_CART_ITEM_IDS,
        ArrayList<String>()
    )

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MallCreateOrderFragment {
            val fragment = MallCreateOrderFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.mall_create_order_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.mall_create_order)
        }
        vRefreshLayout.apply {
            setEnableRefresh(false)
            setEnableLoadMore(false)
        }
        vRefreshList.apply {
            layoutManager = LinearLayoutManager(fragmentActivity)
            adapter = mListAdapter
        }
        //创建订单
        vCreateOrder.click {
            if (mChooseUserAddressInfo.info == null) {
                toast(R.string.mall_please_choose_user_address)
                return@click
            }
            if (mCartItemIds.isEmpty()) {
                return@click
            }
            //弹出支付方式弹窗
            showChoosePayWayDialog()
        }
    }

    override fun setData() {
        super.setData()
        refresh()
    }

    private fun refresh() {
        //先渲染默认数据
        setDefaultData()
        //获取默认地址信息
        getDefaultUserAddress()
        //获取指定购物项的信息列表
        getCartItemsForSettle()
    }

    /**
     * 设置默认数据
     */
    private fun setDefaultData() {
        mListItems.add(mChooseUserAddressInfo)
        mListAdapter.notifyDataSetChanged()
    }

    /**
     * 获取用户的默认收货地址
     */
    private fun getDefaultUserAddress() {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mMallPresenter.getDefaultUserAddress(userId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    httpModel.data?.let {
                        mChooseUserAddressInfo.info = it
                        mListAdapter.notifyDataSetChanged()
                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    /**
     * 获取多个购物项信息
     */
    private fun getCartItemsForSettle() {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mMallPresenter.getCartItemsForSettle(userId, mCartItemIds)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    val list = (httpModel.data ?: mutableListOf()).map {
                        //设置的商品都是选中状态
                        it.isSelect = true
                        it
                    }
                    //先删除，原来的购物车项信息
                    val originItems = mListItems.filterIsInstance<ShoppingCartItemModel>()
                        .toList()
                    if (originItems.isNotEmpty()) {
                        mListItems.removeAll(originItems)
                    }
                    mListItems.addAll(list)
                    mListAdapter.notifyDataSetChanged()
                    //渲染底部栏
                    renderBottomBar()
                }
            }, {
                it.printStackTrace()
                showRequestError()
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
        val totalPrice = if (selectModels.isNotEmpty()) {
            //1、计算总价，并渲染
            val totalPrice = selectModels.map {
                //单价 x 数量 = 一种商品的价格
                it.sellingPrice * it.goodsCount
            }.reduce { sum, nextValue ->
                //每次累计，上一次的累计结果 + 本次的价格 = 总价
                sum + nextValue
            }
            getString(R.string.mall_rmb_price, totalPrice.toString())
        } else {
            //没有一个选中
            getString(R.string.mall_rmb_price, "0")
        }
        vTotalPrice.text = totalPrice
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.run {
                val info =
                    getSerializableExtra(AppConstant.Key.MALL_USER_ADDRESS_INFO) as? UserAddressModel
                info?.let {
                    mChooseUserAddressInfo.info = info
                    mListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * 显示选择支付方式弹窗
     */
    private fun showChoosePayWayDialog() {
        MallPayWayDialog(fragmentActivity, lifecycleOwner).apply {
            setCallback(object : MallPayWayDialog.Callback {
                override fun onClickAlipay() {
                    payNow(PayType.ALI_PAY)
                }

                override fun onClickWxpay() {
                    payNow(PayType.WEI_XIN_PAY)
                }
            })
            show()
        }
    }

    /**
     * 支付
     * @param payType 支付方式
     */
    private fun payNow(payType: PayType) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        val addressId = mChooseUserAddressInfo.info?.addressId
        if (addressId.isNullOrBlank()) {
            return
        }
        mMallPresenter.run {
            //1、创建订单
            saveOrder(userId, mCartItemIds, addressId)
                .doOnSubscribeUi {
                    mWaitLoadingController.showWait()
                }
                .flatMap { httpModel ->
                    if (checkHttpResponse(httpModel)) {
                        httpModel.data?.run {
                            //2、模拟进行支付，支付成功
                            paySuccess(orderNo, payType)
                        }
                    } else {
                        Observable.error(RuntimeException("创建订单失败"))
                    }
                }
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({
                    mWaitLoadingController.hideWait()
                    if (handlerErrorCode(it)) {
                        toast(R.string.mall_pay_success)
                        AppBroadcastManager.sendBroadcast(
                            AppConstant.Action.MALL_PAY_SUCCESS
                        )
                        //跳转到订单列表
                        mMallService?.goMyOrder(fragmentActivity)
                        fragmentActivity.finish()
                    }
                }, {
                    it.printStackTrace()
                    showRequestError()
                    mWaitLoadingController.hideWait()
                })
        }
    }
}