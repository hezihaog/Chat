package com.zh.android.circle.mall.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/19
 * 商品分类模型
 */
data class MallGoodsCategoryModel(
    /**
     * 分类id
     */
    val categoryId: String,
    /**
     * 父级分类id，第一级分类没有父分类
     */
    val parentId: String?,
    /**
     * 分类级别
     */
    val categoryLevel: Int,
    /**
     * 分类名称
     */
    val categoryName: String,
    /**
     * 子分类列表
     */
    val childList: List<MallGoodsCategoryModel>,
    /**
     * 是否选中，非接口字段，用于标识当前是否被选中
     */
    var isSelect: Boolean = false
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}