package com.zh.android.chat.service.module.home

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author wally
 * @date 2020/08/26
 */
interface HomeService : IProvider {
    /**
     * 跳转到首页
     */
    fun goHome(activity: Activity)
}