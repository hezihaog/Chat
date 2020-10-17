package com.zh.android.circle.mall.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/17
 * 商品模型
 */
data class MallGoodsModel(
    /**
     * 商品id
     */
    val goodsId: String,
    /**
     * 商品名称
     */
    val goodsName: String,
    /**
     * 商品简介
     */
    val goodsIntro: String,
    /**
     * 商品图片地址
     */
    val goodsCoverImg: String,
    /**
     * 商品详情字段
     */
    val goodsDetailContent: String,
    /**
     * 商品价格
     */
    val sellingPrice: Int,
    /**
     * 商品标签
     */
    val tag: String,
    /**
     * 商品图片
     */
    val goodsCarouselList: List<String>,
    /**
     * 商品原价
     */
    val originalPrice: Int,
    /**
     * 商品上架状态 1-下架 0-上架
     */
    val goodsSellStatus: Int,
    /**
     * 商品库存数量
     */
    val stockNum: Int
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}