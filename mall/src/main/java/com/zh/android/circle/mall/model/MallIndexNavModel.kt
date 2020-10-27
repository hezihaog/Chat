package com.zh.android.circle.mall.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/17
 * 商城首页导航
 */
data class MallIndexNavModel(
    /**
     * 导航列表
     */
    val navs: List<NavModel>
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }

    /**
     * 分类条目
     */
    data class NavModel(
        /**
         * 导航Id
         */
        val id: String,
        /**
         * 名称
         */
        val name: String,
        /**
         * 导航图片
         */
        val imgUrl: String,
        /**
         * 类型
         */
        val type: String,
        /**
         * 排序值(字段越大越靠前)
         */
        val navRank: Int
    ) : Serializable {
        companion object {
            private const val serialVersionUID = -8591987583696258173L
        }
    }
}