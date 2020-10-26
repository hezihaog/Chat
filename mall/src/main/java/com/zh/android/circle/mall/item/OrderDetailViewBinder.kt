package com.zh.android.circle.mall.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.setGone
import com.zh.android.base.ext.setVisible
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.enums.OrderStatus
import com.zh.android.circle.mall.model.OrderDetailModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/10/26
 * 订单详情条目
 */
class OrderDetailViewBinder(
    /**
     * 点击了去支付
     */
    private val clickPayNowCallback: (model: OrderDetailModel) -> Unit,
    /**
     * 点击了确认收货
     */
    private val clickFinishOrderCallback: (model: OrderDetailModel) -> Unit,
    /**
     * 点击了取消订单
     */
    private val clickCancelOrderCallback: (model: OrderDetailModel) -> Unit
) : ItemViewBinder<OrderDetailModel, OrderDetailViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.mall_order_detail_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: OrderDetailModel) {
        val context = holder.itemView.context
        item.run {
            holder.vOrderStatus.text = orderStatusString
            holder.vOrderNo.text = orderNo
            holder.vPayTime.text = payTime
            holder.vGoodsTotalPrice.text =
                context.resources.getString(R.string.mall_rmb_price, getGoodsTotalPrice(item))
            //去支付
            holder.vPayNow.apply {
                //只有待支付时，才有去支付按钮
                if (orderStatus == OrderStatus.ORDER_PRE_PAY.code) {
                    setVisible()
                } else {
                    setGone()
                }
                click {
                    clickPayNowCallback(item)
                }
            }
            //确认收货
            holder.vFinishOrder.apply {
                //只要状态大于已支付，小于交易完成，才显示确认收货按钮
                if (orderStatus > OrderStatus.ORDER_PAID.code && orderStatus < OrderStatus.ORDER_SUCCESS.code) {
                    setVisible()
                } else {
                    setGone()
                }
                click {
                    clickFinishOrderCallback(item)
                }
            }
            //取消订单
            holder.vCancelOrder.apply {
                //手动关闭、超时关闭、商家关闭时，隐藏取消订单按钮，否则显示
                if (orderStatus == OrderStatus.ORDER_CLOSED_BY_MALL_USER.code ||
                    orderStatus == OrderStatus.ORDER_CLOSED_BY_EXPIRED.code ||
                    orderStatus == OrderStatus.ORDER_CLOSED_BY_JUDGE.code
                ) {
                    setGone()
                } else {
                    setVisible()
                }
                click {
                    clickCancelOrderCallback(item)
                }
            }
            //交易完成，则不显示所有操作按钮
            if (orderStatus == OrderStatus.ORDER_SUCCESS.code) {
                holder.vPayNow.setGone()
                holder.vFinishOrder.setGone()
                holder.vCancelOrder.setGone()
            }
        }
    }

    /**
     * 计算商品总价
     */
    private fun getGoodsTotalPrice(item: OrderDetailModel): String {
        return item.orderItems.map {
            //单价 x 数量 = 一种商品的价格
            it.sellingPrice * it.goodsCount
        }.reduce { sum, nextValue ->
            //每次累计，上一次的累计结果 + 本次的价格 = 总价
            sum + nextValue
        }.toString()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vOrderStatus: TextView = view.findViewById(R.id.order_status)
        val vOrderNo: TextView = view.findViewById(R.id.order_no)
        val vPayTime: TextView = view.findViewById(R.id.pay_time)
        val vGoodsTotalPrice: TextView = view.findViewById(R.id.goods_total_price)
        val vPayNow: TextView = view.findViewById(R.id.pay_now)
        val vFinishOrder: TextView = view.findViewById(R.id.finish_order)
        val vCancelOrder: TextView = view.findViewById(R.id.cancel_order)
    }
}