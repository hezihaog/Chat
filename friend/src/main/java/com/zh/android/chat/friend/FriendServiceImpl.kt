package com.zh.android.chat.friend

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.module.friend.FriendService

/**
 * @author wally
 * @date 2020/08/26
 */
@Route(path = ARouterUrl.FRIEND_SERVICE, name = "好友模块服务")
class FriendServiceImpl : FriendService {
    override fun init(context: Context?) {
    }
}