package com.zh.android.chat.discovery.model

import java.io.Serializable
import java.util.*

/**
 * @author wally
 * @date 2021/02/21
 * 二维码扫描模型
 */
data class QrCodeScanHistoryModel(
    val id: Long,
    /**
     * 创建时间
     */
    var createTime: Date?,
    /**
     * 用户Id
     */
    val userId: String,
    /**
     * 二维码内容
     */
    val qrCodeContent: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}