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

            /**
             * 是否需要验证私密锁
             */
            const val IS_NEED_PATTERN_LOCK = 2000
        }
    }

    class Key private constructor() {
        companion object {
            /**
             * 是否开启侧滑返回
             */
            const val IS_ENABLE_SWIPE_BACK = "is_enable_swipe_back"
            const val USER_ID = "user_id"
            const val USER_NAME = "user_name"
            const val NICK_NAME = "nickname"
            const val AVATAR_URL = "avatar_url"

            /**
             * 是否显示返回键
             */
            const val IS_SHOW_BACK_BTN = "is_show_back_btn"

            /**
             * 聊天记录的Id
             */
            const val CHAT_RECORD_ID = "chat_record_id"

            /**
             * 动态Id
             */
            const val MOMENT_ID = "moment_id"

            /**
             * 是否是我的动态
             */
            const val IS_MY_MOMENT = "moment_is_my_moment"

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
             * 动态发布类型
             */
            const val MOMENT_PUBLISH_TYPE = "moment_publish_type"

            /**
             * 动态数据
             */
            const val MOMENT_INFO = "moment_info"

            /**
             * 通知Id
             */
            const val NOTICE_ID = "notice_id"

            /**
             * 通知的Detail
             */
            const val NOTICE_DETAIL = "notice_detail"

            /**
             * 商品分类Id
             */
            const val MALL_GOODS_CATEGORY_ID = "mall_goods_category_id"

            /**
             * 商品Id
             */
            const val MALL_GOODS_ID = "mall_goods_id"

            /**
             * 订单状态
             */
            const val MALL_ORDER_STATUS = "mall_order_status"

            /**
             * 用户收货地址编辑类型
             */
            const val MALL_USER_ADDRESS_EDIT_TYPE = "mall_user_address_edit_type"

            /**
             * 收货地址Id
             */
            const val MALL_USER_ADDRESS_ID = "mall_user_address_id"

            /**
             * 购物车项Id列表
             */
            const val MALL_CART_ITEM_IDS = "mall_cart_item_ids"

            /**
             * 用户收货地址信息
             */
            const val MALL_USER_ADDRESS_INFO = "mall_user_address_info"

            /**
             * 订单号
             */
            const val MALL_ORDER_NO = "mall_order_no"

            /**
             * 私密锁的请求码
             */
            const val PATTERN_LOCK_ACTION_CODE = "pattern_lock_uuid"

            /**
             * 私密锁是否验证成功
             */
            const val PATTERN_LOCK_IS_VALIDATE = "pattern_lock_is_validate"

            /**
             * 代办事项Id
             */
            const val TODO_ID = "todo_id"

            /**
             * 代办事项编辑类型
             */
            const val TODO_EDIT_TYPE = "todo_edit_type"

            /**
             * 二维码扫描结果
             */
            const val QR_CODE_SCAN_RESULT = "qr_code_scan_result"
        }
    }

    class Action private constructor() {
        companion object {
            /**
             * 切换侧滑关闭
             */
            const val CHANGE_SWIPE_BACK_ENABLE = "action_change_swipe_back_enable"

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

            /**
             * 播放动态视频
             */
            const val MOMENT_PLAY_VIDEO = "action_moment_play_video"

            /**
             * 刷新用户收货地址
             */
            const val MALL_USER_ADDRESS_REFRESH = "action_mall_user_address_refresh"

            /**
             * 支付成功
             */
            const val MALL_PAY_SUCCESS = "action_mall_pay_success"

            /**
             * 刷新订单状态
             */
            const val MALL_REFRESH_ORDER_STATUS = "action_mall_refresh_order_status"

            /**
             * 私密锁验证结果
             */
            const val PATTERN_LOCK_VALIDATE_RESULT = "action_pattern_lock_validate_result"

            /**
             * 刷新代办事项列表
             */
            const val TODO_REFRESH_TODO_LIST = "action_refresh_todo_list"
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

    class Config {
        companion object {
            /**
             * 最多图片数量
             */
            const val MAX_IMAGE_COUNT = Int.MAX_VALUE
        }
    }
}