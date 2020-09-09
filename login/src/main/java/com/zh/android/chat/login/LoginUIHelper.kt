package com.zh.android.chat.login

import android.app.Activity
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.ext.startNavigation

/**
 * @author wally
 * @date 2020/08/26
 */
class LoginUIHelper private constructor() {
    companion object {
        /**
         * 跳转到注册页面
         */
        fun goRegister(activity: Activity, requestCode: Int) {
            ARouter.getInstance()
                .build(ARouterUrl.LOGIN_REGISTER)
                .startNavigation(activity, requestCode)
        }

        /**
         * 跳转到短信登录
         */
        fun goLoginByPhone(activity: Activity) {
            ARouter.getInstance()
                .build(ARouterUrl.LOGIN_BY_PHONE)
                .startNavigation(activity)
        }
    }
}