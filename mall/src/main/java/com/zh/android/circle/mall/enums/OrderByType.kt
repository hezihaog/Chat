package com.zh.android.circle.mall.enums

/**
 * @author wally
 * @date 2020/10/19
 * 排序类型
 */
enum class OrderByType(val type: String) {
    /**
     * 默认按照库存数量从大到小排列
     */
    DEFAULT(""),

    /**
     * 按发布时间的倒序排
     */
    NEW("new"),

    /**
     * 按照售价从小到大排列
     */
    PRICE("price");
}