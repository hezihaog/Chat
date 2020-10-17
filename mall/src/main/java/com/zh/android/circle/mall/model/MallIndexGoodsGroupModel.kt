package com.zh.android.circle.mall.model

import com.zh.android.circle.mall.enums.GoodsGroupType
import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/17
 * 商城首页商品组
 */
data class MallIndexGoodsGroupModel(
    /**
     * 分组类型
     */
    val type: GoodsGroupType,
    /**
     * 分组名
     */
    val groupName: String,
    /**
     * 商品列表
     */
    val goods: List<MallGoodsModel>
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}