package com.zh.android.base.ext

import android.content.Context
import com.zh.android.base.util.DataCleanManager

/**
 * <b>Package:</b> com.tongwei.common.util.ext <br>
 * <b>Create Date:</b> 2019-10-10  09:31 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */

/**
 * 清理缓存
 */
fun Context.clearAppCache() {
    DataCleanManager.cleanApplicationData(this)
}

/**
 * 获取缓存大小
 */
fun Context.getAppCacheSize(): String {
    return DataCleanManager.getCacheSize(this)
}