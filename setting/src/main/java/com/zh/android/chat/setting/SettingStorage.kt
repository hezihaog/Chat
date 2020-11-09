package com.zh.android.chat.setting

import com.blankj.utilcode.util.SPUtils

/**
 * @author wally
 * @date 2020/10/13
 * 设置模块的存储
 */
object SettingStorage {
    /**
     * base url
     */
    private const val KEY_BASE_URL = "setting_key_base_url"

    /**
     * 是否开启侧滑关闭
     */
    private const val KEY_ENABLE_SWIPE_BACK = "setting_key_enable_swipe_back"

    /**
     * 是否开启了私密锁
     */
    private const val KEY_IS_OPEN_PATTERN_LOCK = "setting_key_is_open_pattern_lock"

    @JvmStatic
    fun saveBaseUrl(baseUrl: String) {
        SPUtils.getInstance().put(KEY_BASE_URL, baseUrl)
    }

    @JvmStatic
    fun getBaseUrl(): String {
        return SPUtils.getInstance().getString(KEY_BASE_URL)
    }

    @JvmStatic
    fun saveEnableSwipeBack(enableSwipeBack: Boolean) {
        SPUtils.getInstance().put(KEY_ENABLE_SWIPE_BACK, enableSwipeBack)
    }

    @JvmStatic
    fun isEnableSwipeBack(): Boolean {
        return SPUtils.getInstance().getBoolean(KEY_ENABLE_SWIPE_BACK, true)
    }

    @JvmStatic
    fun savePatternLockString(encryptStr: String) {
        SPUtils.getInstance().put(KEY_IS_OPEN_PATTERN_LOCK, encryptStr)
    }

    @JvmStatic
    fun getPatternLockString(): String {
        return SPUtils.getInstance().getString(KEY_IS_OPEN_PATTERN_LOCK, "")
    }

    @JvmStatic
    fun getIsOpenPatternLock(): Boolean {
        return getPatternLockString().isNotBlank()
    }
}