package com.zh.android.chat.mine.http

import com.zh.android.base.http.HttpModel
import com.zh.android.chat.service.module.mine.model.User
import io.reactivex.Observable

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
     * 更新头像
     * @param userId 用户Id
     * @param avatarUrl 新的头像的Url
     */
    fun updateAvatar(
        userId: String,
        avatarUrl: String
    ): Observable<HttpModel<User>> {
        return MineRequester.updateAvatar(TAG, userId, avatarUrl)
    }

    /**
     * 更新位置信息
     * @param userId 用户Id
     * @param lon 经度
     * @param lat 纬度
     */
    fun updateUserPosition(
        userId: String,
        lon: Double,
        lat: Double
    ): Observable<HttpModel<*>> {
        return MineRequester.updateUserPosition(TAG, userId, lon, lat)
    }
}