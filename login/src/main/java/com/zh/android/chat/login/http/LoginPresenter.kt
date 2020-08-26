package com.zh.android.chat.login.http

import com.apkfuns.logutils.LogUtils
import com.zh.android.base.ext.handlerErrorCode
import com.zh.android.base.http.HttpModel
import com.zh.android.chat.login.LoginStorage
import com.zh.android.chat.login.model.LoginModel
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
                    //保存信息到本地
                    it.result?.let { data ->
                        LogUtils.json(data.toString())
                        LoginStorage.saveUserId(data.id)
                        LoginStorage.saveUsername(data.username)
                    }
                }
            }
    }
}