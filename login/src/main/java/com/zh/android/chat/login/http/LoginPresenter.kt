package com.zh.android.chat.login.http

import com.apkfuns.logutils.LogUtils
import com.zh.android.base.ext.handlerErrorCode
import com.zh.android.base.http.HttpModel
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.chat.login.db.LoginDbMaster
import com.zh.android.chat.login.model.LoginModel
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.db.login.entity.LoginUserEntity
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
                        LoginDbMaster.saveLoginUser(
                            model.id,
                            model.username,
                            model.nickname,
                            model.avatar,
                            model.token
                        )
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
                        LoginDbMaster.saveLoginUser(
                            model.id,
                            model.username,
                            model.nickname,
                            model.avatar,
                            model.token
                        )
                    }
                }
            }
    }

    /**
     * 删除指定用户Id的用户信息
     */
    fun deleteLoginUser(userId: String): Observable<Boolean> {
        return Observable.create {
            LoginDbMaster.deleteLoginUser(userId)
            it.onNext(true)
        }
    }

    /**
     * 获取所有登录账号
     */
    fun getAllLoginUser(): Observable<List<LoginUserEntity>> {
        return Observable.create {
            it.onNext(
                LoginDbMaster.getAllLoginUser()
            )
        }
    }


    /**
     * 切换登录账号
     * @param newLoginUserId 新登录用户信息
     */
    fun switchLoginUser(newLoginUserId: String): Observable<Boolean> {
        return Observable.just(true)
            .doOnSubscribe {
                //发送广播，通知退出登录
                AppBroadcastManager.sendBroadcast(AppConstant.Action.LOGIN_USER_LOGOUT)
            }
            .flatMap<Boolean> {
                //切换账号
                LoginDbMaster.switchLoginUser(newLoginUserId)
                Observable.just(it)
            }.doOnNext {
                //发送广播，通知登录成功
                AppBroadcastManager.sendBroadcast(AppConstant.Action.LOGIN_USER_LOGIN)
            }
    }
}