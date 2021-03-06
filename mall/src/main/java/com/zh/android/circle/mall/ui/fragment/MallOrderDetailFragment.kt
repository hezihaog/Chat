package com.zh.android.circle.mall.ui.fragment

import android.os.Bundle
import android.view.View
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
import com.zh.android.circle.mall.item.OrderDetailViewBinder
import com.zh.android.circle.mall.item.OrderItemViewBinder
import com.zh.android.circle.mall.model.OrderAddressModel
import com.zh.android.circle.mall.model.OrderAddressViewBinder
import com.zh.android.circle.mall.model.OrderDetailModel
import com.zh.android.circle.mall.model.OrderItemModel
import com.zh.android.circle.mall.ui.dialog.MallPayWayDialog
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/26
 * 订单详情
 */
class MallOrderDetailFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.MALL_SERVICE)
    var mMallService: MallService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    /**
     * 订单号
     */
    private val mOrderNo by bindArgument(AppConstant.Key.MALL_ORDER_NO, "")

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            //订单信息条目
            register(OrderDetailModel::class.java, OrderDetailViewBinder({
                //去支付
                showChoosePayWayDialog(it.orderNo)
            }, {
                //确认收货
                finishOrder(it.orderNo)
            }, {
                //取消订单
                cancelOrder(it.orderNo)
            }))
            //收货地址信息条目
            register(OrderAddressModel::class.java, OrderAddressViewBinder())
            //订单项条目
            register(OrderItemModel::class.java, OrderItemViewBinder {
                //跳转到商品详情
                mMallService?.goGoodsDetail(fragmentActivity, it.goodsId)
            })
        }
    }

    private val mWaitLoadingController by lazy {
        WaitLoadingController(fragmentActivity, lifecycleOwner)
    }
    private val mMallPresenter by lazy {
        MallPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MallOrderDetailFragment {
            val fragment = MallOrderDetailFragment()
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
            setTitle(R.string.mall_order_detail)
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
    }

    override fun setData() {
        super.setData()
        refresh()
    }

    private fun refresh() {
        orderDetail()
    }

    /**
     * 获取订单详情
     */
    private fun orderDetail() {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        if (mOrderNo.isBlank()) {
            return
        }
        mMallPresenter.orderDetail(userId, mOrderNo)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                vRefreshLayout.finishRefresh()
                if (handlerErrorCode(httpModel)) {
                    httpModel.data?.let {
                        mListItems.clear()
                        //商品信息条目
                        mListItems.add(it)
                        //订单收货地址
                        mListItems.add(it.orderAddress)
                        //订单项条目
                        mListItems.addAll(it.orderItems)
                        mListAdapter.notifyDataSetChanged()
                    }
                }
            }, {
                it.printStackTrace()
                showRequestError()
                vRefreshLayout.finishRefresh(false)
            })
    }

    /**
     * 显示选择支付方式弹窗
     * @param orderNo 订单号
     */
    private fun showChoosePayWayDialog(orderNo: String) {
        MallPayWayDialog(fragmentActivity, lifecycleOwner).apply {
            setCallback(object : MallPayWayDialog.Callback {
                override fun onClickAlipay() {
                    payNow(orderNo, PayType.ALI_PAY)
                }

                override fun onClickWxpay() {
                    payNow(orderNo, PayType.WEI_XIN_PAY)
                }
            })
            show()
        }
    }

    /**
     * 开始支付
     * @param orderNo 订单号
     * @param payType 支付方式
     */
    private fun payNow(orderNo: String, payType: PayType) {
        mMallPresenter.paySuccess(orderNo, payType)
            .doOnSubscribeUi {
                mWaitLoadingController.showWait()
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
                    //支付完成，刷新页面
                    refresh()
                }
            }, {
                it.printStackTrace()
                showRequestError()
                mWaitLoadingController.hideWait()
            })
    }

    /**
     * 确认收货
     */
    private fun finishOrder(orderNo: String) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mMallPresenter.finishOrder(userId, orderNo)
            .doOnSubscribeUi {
                mWaitLoadingController.showWait()
            }
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({
                mWaitLoadingController.hideWait()
                if (handlerErrorCode(it)) {
                    toast(R.string.mall_finish_order_success)
                    refresh()
                    AppBroadcastManager.sendBroadcast(AppConstant.Action.MALL_REFRESH_ORDER_STATUS)
                }
            }, {
                it.printStackTrace()
                showRequestError()
                mWaitLoadingController.hideWait()
            })
    }

    /**
     * 取消订单
     */
    private fun cancelOrder(orderNo: String) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mMallPresenter.cancelOrder(userId, orderNo)
            .doOnSubscribeUi {
                mWaitLoadingController.showWait()
            }
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({
                mWaitLoadingController.hideWait()
                if (handlerErrorCode(it)) {
                    toast(R.string.base_cancel_success)
                    refresh()
                    AppBroadcastManager.sendBroadcast(AppConstant.Action.MALL_REFRESH_ORDER_STATUS)
                }
            }, {
                it.printStackTrace()
                showRequestError()
                mWaitLoadingController.hideWait()
            })
    }
}