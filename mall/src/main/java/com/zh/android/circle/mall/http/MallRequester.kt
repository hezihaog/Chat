package com.zh.android.circle.mall.http

import com.lzy.okgo.OkGo
import com.lzy.okgo.request.GetRequest
import com.lzy.okgo.request.PostRequest
import com.lzy.okrx2.adapter.ObservableBody
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.genericGsonType
import com.zh.android.base.ext.toJson
import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.ModelConvert
import com.zh.android.base.http.PageModel
import com.zh.android.circle.mall.enums.DefaultAddressFlag
import com.zh.android.circle.mall.enums.OrderByType
import com.zh.android.circle.mall.enums.OrderStatus
import com.zh.android.circle.mall.model.*
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/10/17
 * 商城接口请求
 */
class MallRequester {
    companion object {
        /**
         * 获取商城首页数据
         */
        fun indexInfos(
            tag: String
        ): Observable<HttpModel<MallIndexInfoModel>> {
            val type = genericGsonType<HttpModel<MallIndexInfoModel>>()
            val request: GetRequest<HttpModel<MallIndexInfoModel>> =
                OkGo.get(ApiUrl.MALL_INDEX_INFOS)
            return request.tag(tag)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取商品分类列表
         */
        fun getGoodsCategory(
            tag: String
        ): Observable<HttpModel<List<MallGoodsCategoryModel>>> {
            val type = genericGsonType<HttpModel<List<MallGoodsCategoryModel>>>()
            val request: GetRequest<HttpModel<List<MallGoodsCategoryModel>>> =
                OkGo.get(ApiUrl.MALL_GET_GOODS_CATEGORY)
            return request.tag(tag)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 搜索商品
         * @param keyword 关键字
         * @param goodsCategoryId 商品分类Id
         * @param orderBy 排序方式
         */
        fun searchGoods(
            tag: String,
            keyword: String,
            goodsCategoryId: String,
            orderBy: OrderByType,
            pageNum: Int,
            pageSize: Int
        ): Observable<HttpModel<PageModel<MallGoodsModel>>> {
            val type = genericGsonType<HttpModel<PageModel<MallGoodsModel>>>()
            val request: GetRequest<HttpModel<PageModel<MallGoodsModel>>> =
                OkGo.get(ApiUrl.MALL_GOODS_SEARCH)
            return request.tag(tag)
                .apply {
                    if (keyword.isNotBlank()) {
                        params("keyword", keyword)
                    }
                    if (goodsCategoryId.isNotBlank()) {
                        params("goodsCategoryId", goodsCategoryId)
                    }
                    params("orderBy", orderBy.type)
                    params("pageNum", pageNum)
                    params("pageSize", pageSize)
                }
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取商品详情
         */
        fun getGoodsDetail(
            tag: String,
            goodsId: String
        ): Observable<HttpModel<MallGoodsModel>> {
            val type = genericGsonType<HttpModel<MallGoodsModel>>()
            val request: GetRequest<HttpModel<MallGoodsModel>> =
                OkGo.get(ApiUrl.MALL_GET_GOODS_DETAIL)
            return request.tag(tag)
                .params("goodsId", goodsId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取购物车列表
         */
        fun cartItemList(
            tag: String,
            userId: String
        ): Observable<HttpModel<List<ShoppingCartItemModel>>> {
            val type = genericGsonType<HttpModel<List<ShoppingCartItemModel>>>()
            val request: GetRequest<HttpModel<List<ShoppingCartItemModel>>> =
                OkGo.get(ApiUrl.MALL_CART_ITEM_LIST)
            return request.tag(tag)
                .params("userId", userId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 保存商品到购物车
         * @param goodsId 商品Id
         * @param goodsCount 数量
         */
        fun saveShoppingCartItem(
            tag: String,
            userId: String,
            goodsId: String,
            goodsCount: Int
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.MALL_SAVE_SHOPPING_CART_ITEM)
            return request.tag(tag)
                .params("userId", userId)
                .params("goodsId", goodsId)
                .params("goodsCount", goodsCount)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 更新一项购物车商品信息
         * @param cartItemId 购物车项Id
         * @param goodsCount 数量
         */
        fun updateCartItem(
            tag: String,
            userId: String,
            cartItemId: String,
            goodsCount: Int
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.MALL_UPDATE_CART_ITEM)
            return request.tag(tag)
                .params("userId", userId)
                .params("cartItemId", cartItemId)
                .params("goodsCount", goodsCount)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 删除一项购物车商品信息
         * @param cartItemId 购物车项Id
         */
        fun deleteCartItem(
            tag: String,
            userId: String,
            cartItemId: String
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.MALL_DELETE_CART_ITEM)
            return request.tag(tag)
                .params("userId", userId)
                .params("cartItemId", cartItemId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取购物车列表的数量
         */
        fun cartItemListCount(
            tag: String,
            userId: String
        ): Observable<HttpModel<Int>> {
            val type = genericGsonType<HttpModel<Int>>()
            val request: GetRequest<HttpModel<Int>> =
                OkGo.get(ApiUrl.MALL_CART_ITEM_LIST_COUNT)
            return request.tag(tag)
                .params("userId", userId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取订单列表
         * @param orderStatus 订单状态:0.待支付 1.待确认 2.待发货 3:已发货 4.交易成功
         */
        fun orderList(
            tag: String,
            userId: String,
            orderStatus: OrderStatus,
            pageNum: Int,
            pageSize: Int
        ): Observable<HttpModel<PageModel<OrderListModel>>> {
            val type = genericGsonType<HttpModel<PageModel<OrderListModel>>>()
            val request: GetRequest<HttpModel<PageModel<OrderListModel>>> =
                OkGo.get(ApiUrl.MALL_ORDER_LIST)
            return request.tag(tag)
                .params("userId", userId).apply {
                    //除了默认的，其他状态都带上code
                    if (orderStatus != OrderStatus.DEFAULT) {
                        params("orderStatus", orderStatus.code)
                    }
                }
                .params("pageNum", pageNum)
                .params("pageSize", pageSize)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取我的收货地址列表
         */
        fun getMyAddressList(
            tag: String,
            userId: String
        ): Observable<HttpModel<List<UserAddressModel>>> {
            val type = genericGsonType<HttpModel<List<UserAddressModel>>>()
            val request: GetRequest<HttpModel<List<UserAddressModel>>> =
                OkGo.get(ApiUrl.MALL_GET_MY_ADDRESS_LIST)
            return request.tag(tag)
                .params("userId", userId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 根据Id，获收货地址
         * @param addressId 收货地址Id
         */
        fun getUserAddress(
            tag: String,
            addressId: String
        ): Observable<HttpModel<UserAddressModel>> {
            val type = genericGsonType<HttpModel<UserAddressModel>>()
            val request: GetRequest<HttpModel<UserAddressModel>> =
                OkGo.get(ApiUrl.MALL_GET_USER_ADDRESS)
            return request.tag(tag)
                .params("addressId", addressId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
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
            tag: String,
            userId: String,
            userName: String,
            userPhone: String,
            defaultFlag: DefaultAddressFlag,
            provinceName: String,
            cityName: String,
            regionName: String,
            detailAddress: String
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post<HttpModel<*>>(ApiUrl.MALL_SAVE_USER_ADDRESS)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("userId", userId)
                    put("userName", userName)
                    put("userPhone", userPhone)
                    put("defaultFlag", defaultFlag.code)
                    put("provinceName", provinceName)
                    put("cityName", cityName)
                    put("regionName", regionName)
                    put("regionName", regionName)
                    put("detailAddress", detailAddress)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
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
            tag: String,
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
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post<HttpModel<*>>(ApiUrl.MALL_UPDATE_USER_ADDRESS)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("addressId", addressId)
                    put("userId", userId)
                    put("userName", userName)
                    put("userPhone", userPhone)
                    put("defaultFlag", defaultFlag.code)
                    put("provinceName", provinceName)
                    put("cityName", cityName)
                    put("regionName", regionName)
                    put("regionName", regionName)
                    put("detailAddress", detailAddress)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 删除一个收货地址
         * @param addressId 收货地址Id
         */
        fun deleteAddress(
            tag: String,
            userId: String,
            addressId: String
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post<HttpModel<*>>(ApiUrl.MALL_DELETE_ADDRESS)
            return request.tag(tag)
                .params("userId", userId)
                .params("addressId", addressId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }
    }
}