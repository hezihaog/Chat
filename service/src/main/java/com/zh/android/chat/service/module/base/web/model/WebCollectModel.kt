package com.zh.android.chat.service.module.base.web.model

import java.io.Serializable

/**
 * @author wally
 * @date 2021/02/18
 */
class WebCollectModel(
    /**
     * 收藏Id
     */
    val id: Int,
    /**
     * 用户Id
     */
    val userId: String,
    /**
     * 标题
     */
    val title: String,
    /**
     * Url地址
     */
    val url: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}