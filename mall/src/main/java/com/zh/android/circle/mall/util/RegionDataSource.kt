package com.zh.android.circle.mall.util

import android.content.Context
import com.zh.android.base.util.GetJsonDataUtil
import com.zh.android.base.util.json.JsonProxy
import com.zh.android.circle.mall.model.picker.RegionModel
import org.json.JSONArray

/**
 * @author wally
 * @date 2020/10/21
 * 地区（省、市、区）数据源
 */
object RegionDataSource {
    /**
     * 获取地区数据
     */
    fun getRegionData(context: Context)
            : Triple<List<RegionModel>, List<List<String>>, List<List<List<String>>>> {
        //获取json文件
        val jsonData = GetJsonDataUtil.getJson(context.applicationContext, "province.json")
        //json转换为模型
        val jsonBean = parseData(jsonData)
        //结果
        val options2Items = mutableListOf<List<String>>()
        val options3Items = mutableListOf<List<List<String>>>()
        //遍历省数据
        for (i in jsonBean.indices) {
            ///该省的城市列表（第二级）
            val cityList = mutableListOf<String>()
            //该省的所有地区列表（第三极）
            val provinceAreaList = mutableListOf<List<String>>()
            //遍历该省份的所有城市
            for (c in jsonBean[i].city.indices) {
                val cityName = jsonBean[i].city[c].name
                //添加城市
                cityList.add(cityName)
                //该城市的所有地区列表
                val cityAreaList = mutableListOf<String>()
                cityAreaList.addAll(jsonBean[i].city[c].area);
                //添加该省所有地区数据
                provinceAreaList.add(cityAreaList)
            }
            //添加城市数据
            options2Items.add(cityList)
            //添加地区数据
            options3Items.add(provinceAreaList);
        }
        return Triple(
            jsonBean,
            options2Items,
            options3Items
        )
    }

    /**
     * 解析Json数据为Bean
     */
    private fun parseData(json: String): List<RegionModel> {
        val list = mutableListOf<RegionModel>()
        val data = JSONArray(json)
        //遍历数据
        for (index in 0 until data.length()) {
            //解析
            val obj = data.optJSONObject(index)
            if (obj != null) {
                val str = obj.toString()
                val entity = JsonProxy.get().fromJson(str, RegionModel::class.java)
                list.add(entity)
            }
        }
        return list
    }
}