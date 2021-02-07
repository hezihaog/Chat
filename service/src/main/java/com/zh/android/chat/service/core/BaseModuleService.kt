package com.zh.android.chat.service.core

import android.app.Activity
import com.zh.android.chat.service.module.base.web.WebBrowserActivity

/**
 * @author wally
 * @date 2021/02/07
 * 模块服务基类，所有业务模块的服务都继承该类
 */
abstract class BaseModuleService : IBaseModuleService {
    override fun goInnerWebBrowser(activity: Activity, url: String) {
        WebBrowserActivity.start(activity, url)
    }
}