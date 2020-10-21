package com.zh.android.circle.mall.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/21
 * 订单项模型
 */
data class OrderItemModel(
    /**
     * 商品id
     */
    val goodsId: String,
    /**
     * 商品数量
     */
    val goodsCount: Int,
    /**
     * 商品名称
     */
    val goodsName: String,
    /**
     * 商品图片
     */
    val goodsCoverImg: String,
    /**
     * 商品价格
     */
    val sellingPrice: Int
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}