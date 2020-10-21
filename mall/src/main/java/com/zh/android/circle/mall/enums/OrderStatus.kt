package com.zh.android.circle.mall.enums

/**
 * @author wally
 * @date 2020/10/21
 * 订单状态
 */
enum class OrderStatus(val code: Int) {
    /**
     * 默认，获取所有
     */
    DEFAULT(-1),

    /**
     * 待支付
     */
    ORDER_PRE_PAY(0),

    /**
     * 已支付
     */
    ORDER_PAID(1),

    /**
     * 配货完成
     */
    ORDER_PACKAGED(2),

    /**
     * 出库成功
     */
    ORDER_EXPRESS(3),

    /**
     * 交易成功
     */
    ORDER_SUCCESS(4),

    /**
     * 手动关闭
     */
    ORDER_CLOSED_BY_MALL_USER(-1),

    /**
     * 超时关闭
     */
    ORDER_CLOSED_BY_EXPIRED(-2),

    /**
     * 商家关闭
     */
    ORDER_CLOSED_BY_JUDGE(-3);
}