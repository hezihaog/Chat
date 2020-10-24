package com.zh.android.circle.mall.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/21
 * 用户收货地址模型
 */
data class UserAddressModel(
    /**
     * 地址id
     */
    val addressId: String,
    /**
     * 用户id
     */
    val userId: String,
    /**
     * 收件人名称
     */
    val userName: String,
    /**
     * 收件人联系方式
     */
    val userPhone: String,
    /**
     * 是否默认地址 0-不是 1-是
     */
    val defaultFlag: Int,
    /**
     * 省
     */
    val provinceName: String,
    /**
     * 市
     */
    val cityName: String,
    /**
     * 区/县
     */
    val regionName: String,
    /**
     * 详细地址
     */
    val detailAddress: String,
    /**
     * 是否是编辑，非后端字段，用于UI层显示编辑样式的
     */
    var isEdit: Boolean = false
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}