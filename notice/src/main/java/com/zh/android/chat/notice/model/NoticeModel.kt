package com.zh.android.chat.notice.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/06
 * 通知模型
 */
data class NoticeModel(
    /**
     * 通知id
     */
    val id: String,
    /**
     * 标题
     */
    val title: String,
    /**
     * 内容
     */
    val content: String,
    /**
     * 详情
     */
    val detail: String,
    /**
     * 通知类型，1为系统通知
     */
    val type: Int,
    /**
     * 是否已读
     */
    var read: Boolean,
    /**
     * 创建时间
     */
    val createTime: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}