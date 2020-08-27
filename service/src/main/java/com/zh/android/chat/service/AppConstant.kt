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
            const val USER_ID = "key_user_id"
            const val USER_NAME = "key_user_name"
            const val NICK_NAME = "key_nickname"
        }
    }

    class Action private constructor() {
        companion object {
            const val UPDATE_NICKNAME = "action_update_nickname"
        }
    }
}