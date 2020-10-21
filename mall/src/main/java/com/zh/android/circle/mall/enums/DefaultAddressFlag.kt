package com.zh.android.circle.mall.enums

/**
 * @author wally
 * @date 2020/10/21
 * 默认地址标记
 */
enum class DefaultAddressFlag(val code: Int) {
    /**
     * 不是默认地址
     */
    NO_DEFAULT(0),

    /**
     * 是默认地址
     */
    IS_DEFAULT(1)
}