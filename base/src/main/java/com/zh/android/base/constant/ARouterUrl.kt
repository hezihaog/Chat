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

        /**
         * 手机号登录
         */
        const val LOGIN_BY_PHONE = "${LOGIN_GROUP}/login/phone"

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

        /**
         * 二维码扫描
         */
        const val QR_CODE_SCAN = "${DISCOVERY_GROUP}/qrcode/scan"

        //============================== 动态 ==============================
        private const val MOMENT_GROUP = "/moment"
        const val MOMENT_SERVICE = "${MOMENT_GROUP}s/module"

        /**
         * 动态列表
         */
        const val MOMENT_LIST = "${MOMENT_GROUP}/list"

        /**
         * 只有视频动态的列表
         */
        const val MOMENT_VIDEO_LIST = "${MOMENT_GROUP}/video/list"

        /**
         * 动态详情
         */
        const val MOMENT_DETAIL = "${MOMENT_GROUP}/detail"

        /**
         * 动态发布
         */
        const val MOMENT_PUBLISH = "${MOMENT_GROUP}/publish"

        /**
         * 动态评论详情
         */
        const val MOMENT_COMMENT_DETAIL = "${MOMENT_GROUP}/comment/detail"

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