package com.zh.android.chat.service.core

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author wally
 * @date 2021/02/07
 * 模块服务接口
 */
interface IBaseModuleService : IProvider {
    /**
     * 跳转到内部浏览器
     */
    fun goInnerWebBrowser(activity: Activity, url: String)
}