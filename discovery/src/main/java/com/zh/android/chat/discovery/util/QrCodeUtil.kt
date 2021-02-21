package com.zh.android.chat.discovery.util

import com.blankj.utilcode.util.RegexUtils
import com.zh.android.base.constant.ApiUrl
import com.zh.android.chat.service.AppConstant
import java.net.URI

/**
 * @author wally
 * @date 2021/02/21
 */
class QrCodeUtil private constructor() {
    companion object {
        /**
         * 解析扫描结果
         * @param userQrCodeBlock 用户二维码类型时回调
         * @param webUrlBlock 网页类型时回调
         * @param notHandleBlock 不能识别该类型时回调
         */
        fun parseScanResult(
            result: String,
            userQrCodeBlock: (userId: String) -> Unit,
            webUrlBlock: (url: String) -> Unit,
            notHandleBlock: (result: String) -> Unit
        ) {
            val uri = URI(result)
            when {
                //用户二维码，跳转到用户页面
                uri.scheme == ApiUrl.QR_CODE_SCHEME -> {
                    val path = uri.path
                    val query = uri.query
                    if (path != ApiUrl.QR_CODE_USER_PATH) {
                        return
                    }
                    //按等号，拆分参数
                    val queryArray = query.split("=")
                    //将参数数组转为Map，注意步长要为2
                    val queryMap = mutableMapOf<String, String>().apply {
                        for (index in queryArray.indices step 2) {
                            val key = queryArray[index]
                            val value = queryArray[index + 1]
                            put(key, value)
                        }
                    }
                    //获取传过来的UserId
                    val userId = queryMap[AppConstant.Key.USER_ID]
                    if (userId.isNullOrBlank()) {
                        return
                    }
                    userQrCodeBlock(userId)
                }
                //普通网页
                RegexUtils.isURL(result) -> {
                    webUrlBlock(result)
                }
                //不能识别类型
                else -> {
                    notHandleBlock(result)
                }
            }
        }
    }
}