package com.zh.android.circle.mall.http

import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.PageModel
import com.zh.android.circle.mall.enums.DefaultAddressFlag
import com.zh.android.circle.mall.enums.OrderByType
import com.zh.android.circle.mall.enums.OrderStatus
import com.zh.android.circle.mall.model.*
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/10/17
 * 商城请求封装
 */
class MallPresenter {
    companion object {
        private val TAG = MallPresenter::class.java.simpleName
    }

    /**
     * 获取商城首页数据
     */
    fun indexInfos(): Observable<HttpModel<MallIndexInfoModel>> {
        return MallRequester.indexInfos(TAG)
    }

    /**
     * 获取商品分类列表
     */
    fun getGoodsCategory(): Observable<HttpModel<List<MallGoodsCategoryModel>>> {
        return MallRequester.getGoodsCategory(TAG)
    }

    /**
     * 搜索商品
     * @param keyword 关键字
     * @param goodsCategoryId 商品分类Id
     * @param orderBy 排序方式
     */
    fun searchGoods(
        keyword: String,
        goodsCategoryId: String,
        orderBy: OrderByType,
        pageNum: Int,
        pageSize: Int
    ): Observable<HttpModel<PageModel<MallGoodsModel>>> {
        return MallRequester.searchGoods(TAG, keyword, goodsCategoryId, orderBy, pageNum, pageSize)
    }

    /**
     * 获取商品详情
     */
    fun getGoodsDetail(
        goodsId: String
    ): Observable<HttpModel<MallGoodsModel>> {
        return MallRequester.getGoodsDetail(TAG, goodsId)
    }

    /**
     * 获取购物车列表
     */
    fun cartItemList(
        userId: String
    ): Observable<HttpModel<List<ShoppingCartItemModel>>> {
        return MallRequester.cartItemList(TAG, userId)
    }

    /**
     * 保存商品到购物车
     * @param goodsId 商品Id
     * @param goodsCount 数量
     */
    fun saveShoppingCartItem(
        userId: String,
        goodsId: String,
        goodsCount: Int
    ): Observable<HttpModel<*>> {
        return MallRequester.saveShoppingCartItem(TAG, userId, goodsId, goodsCount)
    }

    /**
     * 更新一项购物车商品信息
     * @param cartItemId 购物车项Id
     * @param goodsCount 数量
     */
    fun updateCartItem(
        userId: String,
        cartItemId: String,
        goodsCount: Int
    ): Observable<HttpModel<*>> {
        return MallRequester.updateCartItem(TAG, userId, cartItemId, goodsCount)
    }

    /**
     * 删除一项购物车商品信息
     * @param cartItemId 购物车项Id
     */
    fun deleteCartItem(
        userId: String,
        cartItemId: String
    ): Observable<HttpModel<*>> {
        return MallRequester.deleteCartItem(TAG, userId, cartItemId)
    }

    /**
     * 获取购物车列表的数量
     */
    fun cartItemListCount(
        userId: String
    ): Observable<HttpModel<Int>> {
        return MallRequester.cartItemListCount(TAG, userId)
    }

    /**
     * 获取订单列表
     * @param orderStatus 订单状态:0.待支付 1.待确认 2.待发货 3:已发货 4.交易成功
     */
    fun orderList(
        userId: String,
        orderStatus: OrderStatus,
        pageNum: Int,
        pageSize: Int
    ): Observable<HttpModel<PageModel<OrderListModel>>> {
        return MallRequester.orderList(TAG, userId, orderStatus, pageNum, pageSize)
    }

    /**
     * 获取我的收货地址列表
     */
    fun getMyAddressList(
        userId: String
    ): Observable<HttpModel<List<UserAddressModel>>> {
        return MallRequester.getMyAddressList(TAG, userId)
    }

    /**
     * 根据Id，获收货地址
     * @param addressId 收货地址Id
     */
    fun getUserAddress(
        addressId: String
    ): Observable<HttpModel<UserAddressModel>> {
        return MallRequester.getUserAddress(TAG, addressId)
    }

    /**
     * 保存一个收货地址
     * @param userName 收件人名称
     * @param userPhone 收件人联系方式
     * @param defaultFlag 是否默认地址 0-不是 1-是
     * @param provinceName 省
     * @param cityName 市
     * @param regionName 区/县
     * @param detailAddress 详细地址
     */
    fun saveUserAddress(
        userId: String,
        userName: String,
        userPhone: String,
        defaultFlag: DefaultAddressFlag,
        provinceName: String,
        cityName: String,
        regionName: String,
        detailAddress: String
    ): Observable<HttpModel<*>> {
        return MallRequester.saveUserAddress(
            TAG,
            userId,
            userName,
            userPhone,
            defaultFlag,
            provinceName,
            cityName,
            regionName,
            detailAddress
        )
    }

    /**
     * 保存一个收货地址
     * @param addressId 地址Id
     * @param userName 收件人名称
     * @param userPhone 收件人联系方式
     * @param defaultFlag 是否默认地址 0-不是 1-是
     * @param provinceName 省
     * @param cityName 市
     * @param regionName 区/县
     * @param detailAddress 详细地址
     */
    fun updateUserAddress(
        addressId: String,
        userId: String,
        userName: String,
        userPhone: String,
        defaultFlag: DefaultAddressFlag,
        provinceName: String,
        cityName: String,
        regionName: String,
        detailAddress: String
    ): Observable<HttpModel<*>> {
        return MallRequester.updateUserAddress(
            TAG,
            addressId,
            userId,
            userName,
            userPhone,
            defaultFlag,
            provinceName,
            cityName,
            regionName,
            detailAddress
        )
    }

    /**
     * 删除一个收货地址
     * @param addressId 收货地址Id
     */
    fun deleteAddress(
        userId: String,
        addressId: String
    ): Observable<HttpModel<*>> {
        return MallRequester.deleteAddress(TAG, userId, addressId)
    }

    /**
     * 获取用户的默认收货地址
     * @param userId 用户Id
     */
    fun getDefaultUserAddress(
        userId: String
    ): Observable<HttpModel<UserAddressModel>> {
        return MallRequester.getDefaultUserAddress(TAG, userId)
    }

    /**
     * 获取多个购物项信息
     * @param userId 用户Id
     * @param cartItemIds 多个购物车项Id
     */
    fun getCartItemsForSettle(
        userId: String,
        cartItemIds: List<String>
    ): Observable<HttpModel<List<ShoppingCartItemModel>>> {
        return MallRequester.getCartItemsForSettle(TAG, userId, cartItemIds)
    }
}