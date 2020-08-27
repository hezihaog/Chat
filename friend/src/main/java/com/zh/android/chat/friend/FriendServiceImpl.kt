package com.zh.android.chat.friend

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.friend.ui.fragment.FriendMainFragment
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.startNavigation
import com.zh.android.chat.service.module.friend.FriendService

/**
 * @author wally
 * @date 2020/08/26
 */
@Route(path = ARouterUrl.FRIEND_SERVICE, name = "好友模块服务")
class FriendServiceImpl : FriendService {
    override fun init(context: Context?) {
    }

    override fun getFriendMainFragment(): String {
        return FriendMainFragment::class.java.name
    }

    override fun goAddFriend(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.FRIEND_ADD_FRIEND)
            .startNavigation(activity)
    }

    override fun goUserProfile(activity: Activity, userId: String) {
        ARouter.getInstance()
            .build(ARouterUrl.FRIEND_USER_PROFILE)
            .withString(AppConstant.Key.USER_ID, userId)
            .startNavigation(activity)
    }
}