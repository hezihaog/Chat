package com.zh.android.chat.service.module.gankio

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author wally
 * @date 2020/12/26
 */
interface GankioService : IProvider {
    /**
     * 跳转到Gankio首页
     */
    fun goGankioHome(activity: Activity)
}