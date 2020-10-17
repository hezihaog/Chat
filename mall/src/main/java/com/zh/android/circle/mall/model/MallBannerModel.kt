package com.zh.android.circle.mall.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/17
 * 轮播图模型
 */
data class MallBannerModel(
    /**
     * 轮播图列表
     */
    val list: List<CarouselModel>
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }

    data class CarouselModel(
        /**
         * 轮播图图片的Url
         */
        val carouselUrl: String,
        /**
         * 点击轮播图后跳转的Url
         */
        val redirectUrl: String
    ) : Serializable {
        companion object {
            private const val serialVersionUID = -1L
        }
    }
}