package com.zh.android.chat.moment.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/09/21
 * 动态发布，默认的添加资源模型
 */
data class AddPublishMediaModel(
    /**
     * 限制多少张图片
     */
    val needCount: Int
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}