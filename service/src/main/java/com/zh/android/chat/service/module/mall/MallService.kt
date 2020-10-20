package com.zh.android.chat.service.module.mall

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author wally
 * @date 2020/10/16
 * 商城模块服务接口
 */
interface MallService : IProvider {
    /**
     * 跳转到原生的商城页面
     */
    fun goMall(activity: Activity)

    /**
     * 跳转到Web的商城页面
     */
    fun goMallWeb(activity: Activity)

    /**
     * 跳转到商品分类
     */
    fun goGoodsCategory(activity: Activity)

    /**
     * 跳转到商品搜索
     * @param goodsCategoryId 商品分类Id，可不传
     */
    fun goGoodsSearch(activity: Activity, goodsCategoryId: String = "")

    /**
     * 跳转到商品详情
     * @param goodsId 商品Id
     */
    fun goGoodsDetail(activity: Activity, goodsId: String)

    /**
     * 跳转到购物车
     */
    fun goShoppingCar(activity: Activity)
}