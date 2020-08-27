package com.zh.android.chat.mine.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/08/27
 * 我的页面，图片型Item条目模型
 */
data class MineImageItemModel(
    /**
     * 条目的Id
     */
    val itemId: Int,
    /**
     * 条目名称
     */
    val itemName: String,
    /**
     * 条目显示的图片的资源Id
     */
    val imageResId: Int,
    /**
     * 图片的Url，加载网络图片时，使用
     */
    val imageUrl: String = "",
    /**
     * 是否可以点击，如果为true，点击时会回调
     */
    val isCanClick: Boolean = false
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}