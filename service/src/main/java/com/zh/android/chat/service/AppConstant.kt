package com.zh.android.chat.service

/**
 * @author wally
 * @date 2020/08/26
 */
class AppConstant private constructor() {
    class Flag private constructor() {
        companion object {
            /**
             * 是否需要登录
             */
            const val IS_NEED_LOGIN = 1000
        }
    }

    class Key private constructor() {
        companion object {
            const val USER_ID = "user_id"
            const val USER_NAME = "user_name"
            const val NICK_NAME = "nickname"
            const val AVATAR_URL = "avatar_url"

            /**
             * 聊天记录的Id
             */
            const val CHAT_RECORD_ID = "chat_record_id"

            /**
             * 动态Id
             */
            const val MOMENT_ID = "moment_id"
        }
    }

    class Action private constructor() {
        companion object {
            /**
             * 用户登录
             */
            const val LOGIN_USER_LOGIN = "action_user_login"
            /**
             * 用户退出登录
             */
            const val LOGIN_USER_LOGOUT = "action_user_logout"
            const val UPDATE_AVATAR = "action_update_avatar"
            const val UPDATE_NICKNAME = "action_update_nickname"
        }
    }

    class HttpParameter {
        companion object {
            /**
             * 平台
             */
            const val PLATFORM = "platform"
            /**
             * 令牌
             */
            const val TOKEN = "token"
            /**
             * 登录账号的用户Id
             */
            const val USER_ID = "userId"
        }
    }
}