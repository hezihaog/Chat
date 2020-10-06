package com.zh.android.chat.friend

import android.app.Activity
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.ext.startNavigation

/**
 * @author wally
 * @date 2020/08/27
 */
class FriendUIHelper private constructor() {
    companion object {
        /**
         * 跳转到好友申请记录
         */
        fun goFriendRequestRecord(activity: Activity) {
            ARouter.getInstance()
                .build(ARouterUrl.FRIEND_REQUEST_RECORD)
                .startNavigation(activity)
        }

        /**
         * 跳转到附近的人
         */
        fun goVicinityUser(activity: Activity) {
            ARouter.getInstance()
                .build(ARouterUrl.FRIEND_VICINITY_USER)
                .startNavigation(activity)
        }
    }
}