package com.zh.android.chat.login

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.base.util.activity.ActivityProvider
import com.zh.android.chat.service.AppConstant
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

    override fun goLogin() {
        ARouter.getInstance()
            .build(ARouterUrl.LOGIN_LOGIN)
            .navigation()
    }

    override fun logout() {
        //清除所有界面
        ActivityProvider.get().finishAllActivity()
        //跳转到登录
        goLogin()
        //清除登录信息
        LoginStorage.clean()
        //通知其他模块
        AppBroadcastManager.sendBroadcast(AppConstant.Action.LOGIN_USER_LOGOUT)
    }
}