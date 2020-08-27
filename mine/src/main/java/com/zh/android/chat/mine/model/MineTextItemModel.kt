package com.zh.android.chat.mine.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/08/27
 * 我的页面，文字型Item条目模型
 */
data class MineTextItemModel(
    /**
     * 条目的Id
     */
    val itemId: Int,
    /**
     * 条目名称
     */
    val itemName: String,
    /**
     * 条目显示的文字
     */
    val text: String,
    /**
     * 是否可以点击，如果为true，点击时会回调
     */
    val isCanClick: Boolean = false
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}