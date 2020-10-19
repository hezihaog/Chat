package com.zh.android.circle.mall.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/19
 * 商品详情-Web信息条目
 */
data class GoodsWebDetailModel(
    /**
     * Web富文本的Html信息
     */
    val goodsDetailContent: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}