package com.zh.android.chat.moment.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/09/21
 * 动态发布，资源实体模型
 */
class MomentPublishMediaModel(
    /**
     * 媒体的Url
     */
    val url: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}