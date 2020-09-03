package com.zh.android.base.constant

/**
 * @author wally
 * @date 2020/08/26
 */
class ARouterUrl {
    companion object {
        const val IS_LOGIN_INTERCEPTOR = "is_login_interceptor"

        //============================== 登录 ==============================
        private const val LOGIN_GROUP = "/login"
        const val LOGIN_SERVICE = "${LOGIN_GROUP}s/module"

        /**
         * 登录
         */
        const val LOGIN_LOGIN = "$LOGIN_GROUP/login"

        /**
         * 注册
         */
        const val LOGIN_REGISTER = "$LOGIN_GROUP/register"

        //============================== 首页 ==============================
        private const val HOME_GROUP = "/home"
        const val HOME_SERVICE = "${HOME_GROUP}s/module"

        /**
         * 首页
         */
        const val HOME_HOME = "${HOME_GROUP}/home"

        //============================== 会话 ==============================
        private const val CONVERSATION_GROUP = "/conversation"
        const val CONVERSATION_SERVICE = "${CONVERSATION_GROUP}s/module"

        /**
         * 聊天会话
         */
        const val CONVERSATION_CHAT = "${CONVERSATION_GROUP}/chat"

        //============================== 好友 ==============================
        private const val FRIEND_GROUP = "/friend"
        const val FRIEND_SERVICE = "${FRIEND_GROUP}s/module"

        /**
         * 添加好友
         */
        const val FRIEND_ADD_FRIEND = "${FRIEND_GROUP}/add/friend"

        /**
         * 用户资料
         */
        const val FRIEND_USER_PROFILE = "${FRIEND_GROUP}/user/profile"

        /**
         * 好友申请记录
         */
        const val FRIEND_REQUEST_RECORD = "${FRIEND_GROUP}/friend/request"

        //============================== 发现 ==============================
        private const val DISCOVERY_GROUP = "/discovery"
        const val DISCOVERY_SERVICE = "${DISCOVERY_GROUP}s/module"

        //============================== 我的 ==============================
        private const val MINE_GROUP = "/mine"
        const val MINE_SERVICE = "${MINE_GROUP}s/module"

        /**
         * 修改昵称
         */
        const val MINE_MODIFY_NICKNAME = "${MINE_GROUP}/modify/nickname"

        /**
         * 修改头像
         */
        const val MINE_MODIFY_AVATAR = "${MINE_GROUP}/modify/avatar"

        /**
         * 我的二维码
         */
        const val MINE_MY_QR_CODE = "${MINE_GROUP}/my/qrcode"

        //============================== 设置 ==============================
        private const val SETTING_GROUP = "/setting"
        const val SETTING_SERVICE = "${SETTING_GROUP}s/module"

        /**
         * 设置首页
         */
        const val SETTING_APP = "$SETTING_GROUP/main"
    }
}