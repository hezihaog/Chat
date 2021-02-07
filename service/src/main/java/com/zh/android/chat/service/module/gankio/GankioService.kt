package com.zh.android.chat.service.module.gankio

import android.app.Activity
import com.zh.android.chat.service.core.IBaseModuleService

/**
 * @author wally
 * @date 2020/12/26
 */
interface GankioService : IBaseModuleService {
    /**
     * 跳转到Gankio首页
     */
    fun goGankioHome(activity: Activity)
}