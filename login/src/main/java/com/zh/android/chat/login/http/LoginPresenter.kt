package com.zh.android.chat.login.http

import com.apkfuns.logutils.LogUtils
import com.zh.android.base.ext.handlerErrorCode
import com.zh.android.base.http.HttpModel
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.chat.login.db.master.LoginDbMaster
import com.zh.android.chat.login.model.LoginModel
import com.zh.android.chat.service.AppConstant
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/08/26
 */
class LoginPresenter {
    companion object {
        private val TAG = LoginPresenter::class.java.simpleName
    }

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     */
    fun login(
        username: String,
        password: String
    ): Observable<HttpModel<LoginModel>> {
        return LoginRequester.login(TAG, username, password)
            .doOnNext {
                if (handlerErrorCode(it)) {
                    //保存信息到数据库
                    it.data?.let { model ->
                        LogUtils.json(model.toString())
                        LoginDbMaster.saveLoginUser(model.id, model.username, model.token)
                    }
                    //通知其他模块
                    AppBroadcastManager.sendBroadcast(AppConstant.Action.LOGIN_USER_LOGIN)
                }
            }
    }

    /**
     * 注册
     * @param username 用户名
     * @param password 密码
     */
    fun register(
        username: String,
        password: String
    ): Observable<HttpModel<*>> {
        return LoginRequester.register(TAG, username, password)
    }

    /**
     * 获取验证码
     * @param telephone 手机号
     */
    fun getAuthCode(
        telephone: String
    ): Observable<HttpModel<String>> {
        return LoginRequester.getAuthCode(TAG, telephone)
    }

    /**
     * 验证码登录
     * @param telephone 手机号
     * @param authCode  验证码
     */
    fun loginByAuthCode(
        telephone: String,
        authCode: String
    ): Observable<HttpModel<LoginModel>> {
        return LoginRequester.loginByAuthCode(TAG, telephone, authCode)
            .doOnNext {
                if (handlerErrorCode(it)) {
                    //保存信息到数据库
                    it.data?.let { model ->
                        LogUtils.json(model.toString())
                        LoginDbMaster.saveLoginUser(model.id, model.username, model.token)
                    }
                }
            }
    }
}