package com.zh.android.circle.mall.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/21
 * 订单详情模型
 */
data class OrderDetailModel(
    /**
     * 订单号
     */
    val orderNo: String,
    /**
     * 订单价格
     */
    val totalPrice: Int,
    /**
     * 订单支付状态码
     */
    val payStatus: Int,
    /**
     * 订单支付方式
     */
    val payType: Int,
    /**
     * 订单支付方式
     */
    val payTypeString: String,
    /**
     * 订单支付时间
     */
    val payTime: String?,
    /**
     * 订单状态码
     */
    val orderStatus: Int,
    /**
     * 订单状态
     */
    val orderStatusString: String,
    /**
     * 创建时间
     */
    val createTime: String,
    /**
     * 收货地址信息
     */
    val orderAddress: OrderAddressModel,
    /**
     * 订单项列表
     */
    val orderItems: List<OrderItemModel>
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}