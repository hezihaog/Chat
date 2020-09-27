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

            /**
             * 动态的评论Id
             */
            const val MOMENT_COMMENT_ID = "moment_comment_id"

            /**
             * 是否点赞
             */
            const val MOMENT_IS_LIKE = "moment_is_like"

            /**
             * 动态点赞数量
             */
            const val MOMENT_LIKE_NUM = "moment_like_num"

            /**
             * 只有动态文字
             */
            const val MOMENT_ONLY_TEXT = "moment_only_text"
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

            /**
             * 更新头像
             */
            const val UPDATE_AVATAR = "action_update_avatar"

            /**
             * 更新昵称
             */
            const val UPDATE_NICKNAME = "action_update_nickname"

            /**
             * 发布动态成功
             */
            const val MOMENT_PUBLISH_SUCCESS = "action_moment_publish_success"

            /**
             * 评论动态成功
             */
            const val MOMENT_ADD_COMMENT_SUCCESS = "action_moment_add_comment"

            /**
             * 动态点赞切换
             */
            const val MOMENT_LIKE_CHANGE = "action_moment_like_change"

            /**
             * 动态转发成功
             */
            const val MOMENT_FORWARD_SUCCESS = "action_moment_forward_success"

            /**
             * 删除动态成功
             */
            const val MOMENT_DELETE_SUCCESS = "action_moment_delete_success"

            /**
             * 刷新动态详情
             */
            const val MOMENT_DETAIL_REFRESH = "action_moment_detail_refresh"
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