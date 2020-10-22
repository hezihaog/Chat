package com.zh.android.chat.moment.enums

/**
 * @author wally
 * @date 2020/10/22
 * 动态公开标记，0为公开，1为私密
 */
enum class PublicFlag(val code: Int) {
    /**
     * 公开
     */
    PUBLIC(0),
    /**
     * 私密
     */
    PRIVACY(1);
}