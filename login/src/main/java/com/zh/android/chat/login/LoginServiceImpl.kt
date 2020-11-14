package com.zh.android.chat.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.base.util.activity.ActivityProvider
import com.zh.android.chat.login.db.LoginDbMaster
import com.zh.android.chat.login.ui.activity.LoginActivity
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
        return LoginDbMaster.getCurrentLoginUser()?.loginFlag == true
    }

    override fun getUserId(): String {
        return LoginDbMaster.getCurrentLoginUser()?.userId ?: ""
    }

    override fun getToken(): String {
        return LoginDbMaster.getCurrentLoginUser()?.token ?: ""
    }

    override fun savePatternLockStr(encryptStr: String) {
        LoginDbMaster.savePatternLockStr(encryptStr)
    }

    override fun getPatternLockStr(): String {
        return LoginDbMaster.getPatternLockStr()
    }

    override fun saveIsOpenPatternLock(isOpen: Boolean) {
        LoginDbMaster.saveIsOpenPatternLock(isOpen)
    }

    override fun isOpenPatternLock(): Boolean {
        return LoginDbMaster.isOpenPatternLock()
    }

    override fun goLogin(
        activity: Activity,
        isClearOther: Boolean,
        isShowBackBtn: Boolean,
        callback: NavigationCallback?
    ) {
        ARouter.getInstance()
            .build(ARouterUrl.LOGIN_LOGIN).apply {
                if (isClearOther) {
                    //清除其他页面
                    withFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    //去掉跳转动画
                    withTransition(0, 0)
                }
            }
            //是否显示返回键
            .withBoolean(AppConstant.Key.IS_SHOW_BACK_BTN, isShowBackBtn)
            .startNavigation(activity, callback = object : NavigationCallback {
                override fun onFound(postcard: Postcard?) {
                    callback?.onFound(postcard)
                }

                override fun onLost(postcard: Postcard?) {
                    callback?.onLost(postcard)
                }

                override fun onArrival(postcard: Postcard?) {
                    callback?.onArrival(postcard)
                    //清除其他页面
                    if (isClearOther) {
                        ActivityProvider.get().finishOtherActivity(LoginActivity::class.java)
                    }
                }

                override fun onInterrupt(postcard: Postcard?) {
                    callback?.onInterrupt(postcard)
                }
            })
    }

    override fun goSwitchLoginAccount(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.LOGIN_SWITCH_LOGIN_ACCOUNT)
            .startNavigation(activity)
    }

    override fun logout(activity: Activity) {
        //跳转到登录，并关闭其他页面
        goLogin(
            activity,
            isClearOther = true,
            isShowBackBtn = false,
            callback = object : NavCallback() {
                override fun onArrival(postcard: Postcard?) {
                    //数据库清空登录标记
                    LoginDbMaster.logout()
                    //通知其他模块
                    AppBroadcastManager.sendBroadcast(AppConstant.Action.LOGIN_USER_LOGOUT)
                }
            })
    }
}