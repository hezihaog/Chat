package com.zh.android.circle.mall.model.picker

import com.contrarywind.interfaces.IPickerViewData
import java.io.Serializable

/**
 * @author wally
 * @date 2020/10/21
 * 地区数据
 */
data class RegionModel(
    val name: String,
    val city: List<CityModel>
) : IPickerViewData, Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }

    /**
     * 实现 IPickerViewData 接口
     * 这个用来显示在PickerView上面的字符串
     * PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
     */
    override fun getPickerViewText(): String {
        return this.name
    }

    data class CityModel(
        /**
         * name : 城市
         * area : ["东城区","西城区","崇文区","昌平区"]
         */
        val name: String,
        val area: List<String>
    ) : Serializable {
        companion object {
            private const val serialVersionUID = -8591987583696258173L
        }
    }
}