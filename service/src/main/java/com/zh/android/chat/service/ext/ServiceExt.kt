package com.zh.android.chat.service.ext

import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.module.conversation.ConversationService
import com.zh.android.chat.service.module.discovery.DiscoveryService
import com.zh.android.chat.service.module.friend.FriendService
import com.zh.android.chat.service.module.home.HomeService
import com.zh.android.chat.service.module.login.LoginService
import com.zh.android.chat.service.module.mine.MineService
import com.zh.android.chat.service.module.moment.MomentService
import com.zh.android.chat.service.module.notice.NoticeService
import com.zh.android.chat.service.module.setting.SettingService

/**
 * @author wally
 * @date 2020/08/26
 */

/**
 * 获取登录模块服务
 */
fun getLoginService(): LoginService? {
    return ARouter.getInstance().build(ARouterUrl.LOGIN_SERVICE).navigation() as? LoginService
}

/**
 * 获取首页模块服务
 */
fun getHomeService(): HomeService? {
    return ARouter.getInstance().build(ARouterUrl.HOME_SERVICE).navigation() as? HomeService
}

/**
 * 获取会话模块服务
 */
fun getConversationService(): ConversationService? {
    return ARouter.getInstance().build(ARouterUrl.CONVERSATION_SERVICE)
        .navigation() as? ConversationService
}

/**
 * 获取好友模块服务
 */
fun getFriendService(): FriendService? {
    return ARouter.getInstance().build(ARouterUrl.FRIEND_SERVICE).navigation() as? FriendService
}

/**
 * 获取发现模块服务
 */
fun getDiscoveryService(): DiscoveryService? {
    return ARouter.getInstance().build(ARouterUrl.DISCOVERY_SERVICE)
        .navigation() as? DiscoveryService
}

/**
 * 获取动态模块服务
 */
fun getMomentService(): MomentService? {
    return ARouter.getInstance().build(ARouterUrl.MOMENT_SERVICE)
        .navigation() as? MomentService
}

/**
 * 获取我的模块服务
 */
fun getMineService(): MineService? {
    return ARouter.getInstance().build(ARouterUrl.MINE_SERVICE).navigation() as? MineService
}

/**
 * 获取设置模块服务
 */
fun getSettingService(): SettingService? {
    return ARouter.getInstance().build(ARouterUrl.SETTING_SERVICE).navigation() as? SettingService
}

/**
 * 获取通知模块服务
 */
fun getNoticeService(): NoticeService? {
    return ARouter.getInstance().build(ARouterUrl.NOTICE_SERVICE)
        .navigation() as? NoticeService
}