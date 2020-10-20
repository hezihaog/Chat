package com.zh.android.circle.mall.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/20
 * 购物车项模型
 */
data class ShoppingCartItemModel(
    /**
     * 购物车项id
     */
    val cartItemId: String,
    /**
     * 商品id
     */
    val goodsId: String,
    /**
     * 商品数量
     */
    var goodsCount: Int,
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
    val sellingPrice: Int,
    /**
     * 非接口字段，用来标识当前条目是否被选中
     */
    var isSelect: Boolean
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}