package com.zh.android.base.core

import com.blankj.utilcode.util.SPUtils

/**
 * @author wally
 * @date 2020/12/18
 */
object BaseStorage {
    /**
     * BaseUrl
     */
    private const val KEY_BASE_URL = "base_url"

    @JvmStatic
    fun saveBaseUrl(baseUrl: String) {
        SPUtils.getInstance().put(KEY_BASE_URL, baseUrl)
    }

    @JvmStatic
    fun getBaseUrl(): String {
        return SPUtils.getInstance().getString(KEY_BASE_URL, "")
    }
}