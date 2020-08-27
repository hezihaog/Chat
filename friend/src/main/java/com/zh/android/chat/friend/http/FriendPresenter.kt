package com.zh.android.chat.friend.http

import com.zh.android.base.http.HttpModel
import com.zh.android.chat.friend.model.FriendRequest
import com.zh.android.chat.service.module.mine.model.User
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/08/27
 * 好友模块 请求
 */
class FriendPresenter {
    companion object {
        private val TAG = FriendPresenter::class.java.name
    }

    /**
     * 查询用户的好友列表
     * @param userId 用户Id
     */
    fun getUserFriendList(
        userId: String
    ): Observable<HttpModel<List<User>>> {
        return FriendRequester.getUserFriendList(TAG, userId)
    }

    /**
     * 根据用户名搜索用户信息
     * @param username 用户名
     */
    fun findUserByUsername(
        username: String
    ): Observable<HttpModel<User>> {
        return FriendRequester.findUserByUsername(TAG, username)
    }

    /**
     * 发送好友请求
     * @param fromUserId 发送请求的用户Id
     * @param toUserId 要添加的用户Id
     */
    fun sendFriendRequest(
        fromUserId: String,
        toUserId: String
    ): Observable<HttpModel<*>> {
        return FriendRequester.sendFriendRequest(TAG, fromUserId, toUserId)
    }

    /**
     * 查找某个用户Id的所有好友请求
     */
    fun getUserAllFriendRequest(
        userId: String
    ): Observable<HttpModel<List<FriendRequest>>> {
        return FriendRequester.getUserAllFriendRequest(TAG, userId)
    }
}