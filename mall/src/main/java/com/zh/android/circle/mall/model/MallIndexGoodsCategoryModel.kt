package com.zh.android.circle.mall.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/17
 * 首页分类模型
 */
data class MallIndexGoodsCategoryModel(
    /**
     * 分类列表
     */
    val list: List<CategoryModel>
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }

    /**
     * 分类条目
     */
    data class CategoryModel(
        /**
         * 分类名称
         */
        val name: String,
        /**
         * 图片地址
         */
        val imgUrl: String
    ) : Serializable {
        companion object {
            private const val serialVersionUID = -8591987583696258173L
        }
    }
}