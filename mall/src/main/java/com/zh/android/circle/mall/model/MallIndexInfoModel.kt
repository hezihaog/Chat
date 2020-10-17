package com.zh.android.circle.mall.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/17
 * 商城首页数据
 */
data class MallIndexInfoModel(
    /**
     * 轮播图
     */
    val carousels: List<MallBannerModel.CarouselModel>,
    /**
     * 最热商品
     */
    val hotGoods: List<MallGoodsModel>,
    /**
     * 最新商品
     */
    val newGoods: List<MallGoodsModel>,
    /**
     * 推荐商品
     */
    val recommendGoods: List<MallGoodsModel>
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}