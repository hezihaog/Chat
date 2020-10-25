package com.zh.android.circle.mall.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/25
 * 创建订单后，返回的信息模型
 */
data class SaveOrderResultModel(
    /**
     * 订单Id
     */
    val orderId: String,
    /**
     * 订单号
     */
    val orderNo: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}