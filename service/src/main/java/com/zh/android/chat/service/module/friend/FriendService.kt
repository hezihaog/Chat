package com.zh.android.chat.service.module.friend

import android.app.Activity
import com.zh.android.chat.service.core.IBaseModuleService

/**
 * @author wally
 * @date 2020/08/26
 */
interface FriendService : IBaseModuleService {
    /**
     * 获取好友首页Fragment
     */
    fun getFriendMainFragment(): String

    /**
     * 跳转到添加好友页面
     */
    fun goAddFriend(activity: Activity)

    /**
     * 跳转到用户详情
     * @param userId 用户Id
     */
    fun goUserProfile(activity: Activity, userId: String)
}