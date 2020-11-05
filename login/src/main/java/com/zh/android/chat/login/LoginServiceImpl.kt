package com.zh.android.chat.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.startNavigation
import com.zh.android.chat.service.module.login.LoginService

/**
 * @author wally
 * @date 2020/08/26
 * 登录服务实现
 */
@Route(path = ARouterUrl.LOGIN_SERVICE, name = "登录模块服务")
class LoginServiceImpl : LoginService {
    override fun init(context: Context?) {
    }

    override fun isLogin(): Boolean {
        return LoginStorage.isLogin()
    }

    override fun getUserId(): String {
        return LoginStorage.getUserId()
    }

    override fun getToken(): String {
        return LoginStorage.getToken()
    }

    override fun goLogin(activity: Activity, isClearOther: Boolean, callback: NavigationCallback?) {
        ARouter.getInstance()
            .build(ARouterUrl.LOGIN_LOGIN).apply {
                if (isClearOther) {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            }
            .startNavigation(activity, callback = callback)
    }

    override fun logout(activity: Activity) {
        //清除登录信息
        LoginStorage.clean()
        //通知其他模块
        AppBroadcastManager.sendBroadcast(AppConstant.Action.LOGIN_USER_LOGOUT)
        //跳转到登录，并关闭其他页面
        goLogin(activity, isClearOther = true)
    }
}