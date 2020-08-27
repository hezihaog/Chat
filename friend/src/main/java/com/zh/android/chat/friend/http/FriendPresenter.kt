package com.zh.android.chat.friend.http

import com.zh.android.base.http.HttpModel
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
}