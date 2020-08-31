package com.zh.android.chat.mine.http

import com.zh.android.base.http.HttpModel
import com.zh.android.chat.service.module.mine.model.User
import io.reactivex.Observable
import java.io.File

/**
 * @author wally
 * @date 2020/08/27
 * 我的模块，请求
 */
class MinePresenter {
    companion object {
        private val TAG = MinePresenter::class.java.name
    }

    /**
     * 根据用户Id获取用户信息
     * @param userId 用户Id
     */
    fun getUserInfo(
        userId: String
    ): Observable<HttpModel<User>> {
        return MineRequester.getUserInfo(TAG, userId)
    }

    /**
     * 更新昵称
     * @param userId 用户Id
     * @param newNickName 新的昵称
     */
    fun updateNickName(
        userId: String,
        newNickName: String
    ): Observable<HttpModel<*>> {
        return MineRequester.updateNickName(TAG, userId, newNickName)
    }

    /**
     * 上传头像
     * @param userId 用户Id
     * @param avatarFile 新的头像File对象
     */
    fun uploadAvatar(
        userId: String,
        avatarFile: File
    ): Observable<HttpModel<User>> {
        return MineRequester.uploadAvatar(TAG, userId, avatarFile)
    }
}