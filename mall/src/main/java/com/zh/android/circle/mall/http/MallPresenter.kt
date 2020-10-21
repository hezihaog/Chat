package com.zh.android.circle.mall.http

import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.PageModel
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
}