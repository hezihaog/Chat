package com.zh.android.chat.friend.enums

/**
 * @author wally
 * @date 2020/10/06
 * 好友申请状态
 */
enum class FriendRequestStatus(val code: Int) {
    /**
     * 已申请，等待同意
     */
    WAIT_OP(0),

    /**
     * 已操作
     */
    OP(1)
}