package com.zh.android.chat.moment.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/09/21
 * 动态发布，图片实体模型
 */
class MomentPublishImageModel(
    /**
     * 图片的Url
     */
    val url: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}