package com.zh.android.chat.service.module.base.model

import java.io.File
import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/23
 * 图片信息模型
 */
data class ImageInfoModel(
    /**
     * 图片文件
     */
    val file: File,
    /**
     * 图片宽度
     */
    val width: Int,
    /**
     * 图片高度
     */
    val height: Int
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}