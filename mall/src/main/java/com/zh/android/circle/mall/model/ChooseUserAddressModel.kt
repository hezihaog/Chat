package com.zh.android.circle.mall.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/24
 * 选择用户收货地址模型
 */
data class ChooseUserAddressModel(
    /**
     * 选择的收货地址信息，当选择了的时候才有值
     */
    var info: UserAddressModel? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}