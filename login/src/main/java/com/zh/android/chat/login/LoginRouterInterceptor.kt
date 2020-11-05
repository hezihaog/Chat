package com.zh.android.chat.login

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.AppConstant

/**
 * @author wally
 * @date 2020/08/26
 * 跳转时的登录拦截器
 */
@Interceptor(priority = 1)
class LoginRouterInterceptor : IInterceptor {
    override fun init(context: Context?) {
    }

    override fun process(postcard: Postcard, callback: InterceptorCallback) {
        //获取页面是否需要登录才可以跳转
        val isNeedInterceptor = postcard.extra == AppConstant.Flag.IS_NEED_LOGIN
        //是否是登录页面
        val isLoginPage = postcard.path == ARouterUrl.LOGIN_LOGIN
        if (isLoginPage) {
            //登录页不需要拦截
            callback.onContinue(postcard)
        } else {
            if (isNeedInterceptor) {
                val isLogin: Boolean = LoginStorage.isLogin()
                //没有登录，拦截
                if (!isLogin) {
                    //加一个被拦截的标识代表被登录拦截
                    postcard.withBoolean(ARouterUrl.IS_LOGIN_INTERCEPTOR, true)
                    callback.onInterrupt(null)
                } else {
                    callback.onContinue(postcard)
                }
            } else {
                //不需要登录的页面，放行
                callback.onContinue(postcard)
            }
        }
    }
}