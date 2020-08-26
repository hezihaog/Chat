package com.zh.android.chat.login

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
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
}