package com.zh.android.base.http

import java.io.Serializable

/**
 * @author wally
 * @date 2020/09/19
 */
open class PageModel<T>(
    /**
     * 总行数
     */
    val total: Long,
    /**
     * 总页数
     */
    val pages: Long,
    /**
     * 数据列表
     */
    val list: List<T>
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}