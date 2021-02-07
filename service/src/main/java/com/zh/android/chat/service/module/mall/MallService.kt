package com.zh.android.chat.service.module.mall

import android.app.Activity
import com.zh.android.chat.service.core.IBaseModuleService
import com.zh.android.chat.service.module.mall.enums.UserAddressEditType

/**
 * @author wally
 * @date 2020/10/16
 * 商城模块服务接口
 */
interface MallService : IBaseModuleService {
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

    /**
     * 跳转到我的订单
     */
    fun goMyOrder(activity: Activity)

    /**
     * 跳转到用户收货地址管理
     */
    fun goUserAddressManage(activity: Activity)

    /**
     * 跳转到用户收货地址编辑
     * @param type 编辑类型，新增或更新
     * @param addressId 地址Id，只在更新时有值
     */
    fun goUserAddressEdit(activity: Activity, type: UserAddressEditType, addressId: String = "")

    /**
     * 跳转到创建订单
     * @param cartItemIds 购物车项Id数组
     */
    fun goCreateOrder(activity: Activity, cartItemIds: ArrayList<String>)

    /**
     * 跳转到选择用户地址
     */
    fun goChooseUserAddress(activity: Activity)

    /**
     * 跳转到订单详情
     * @param orderNo 订单Id
     */
    fun goOrderDetail(activity: Activity, orderNo: String)
}