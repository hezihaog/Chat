package com.zh.android.chat.service.module.base.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/23
 * 上传图片时需要附带的信息
 */
data class UploadImageInfoModel(
    /**
     * 图片信息列表
     */
    var infos: List<ImageInfoModel>
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}