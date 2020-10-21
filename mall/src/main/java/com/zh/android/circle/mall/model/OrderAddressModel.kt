package com.zh.android.circle.mall.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/21
 * 订单收货地址信息模型
 */
class OrderAddressModel(
    /**
     * 收货人姓名
     */
    val userName: String,
    /**
     * 收货人手机号
     */
    val userPhone: String,
    /**
     * 省
     */
    val provinceName: String,
    /**
     * 城
     */
    val cityName: String,
    /**
     * 区
     */
    val regionName: String,
    /**
     * 收件详细地址(街道/楼宇/单元)
     */
    val detailAddress: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}