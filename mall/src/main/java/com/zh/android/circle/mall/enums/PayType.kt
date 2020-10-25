package com.zh.android.circle.mall.enums

/**
 * @author wally
 * @date 2020/10/25
 * 支付方式
 */
enum class PayType(val code: Int, val desc: String) {
    DEFAULT(-1, "ERROR"),
    NOT_PAY(0, "无"),
    ALI_PAY(1, "支付宝"),
    WEI_XIN_PAY(2, "微信支付");
}