package com.zh.android.chat.home

import android.app.Activity
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl

/**
 * @author wally
 * @date 2020/08/26
 */
class HomeUIHelper private constructor() {
    companion object {
        fun goHome(activity: Activity) {
            ARouter.getInstance()
                .build(ARouterUrl.HOME_HOME)
                .navigation(activity)
        }
    }
}