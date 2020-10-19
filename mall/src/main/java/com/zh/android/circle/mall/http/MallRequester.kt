package com.zh.android.circle.mall.http

import com.lzy.okgo.OkGo
import com.lzy.okgo.request.GetRequest
import com.lzy.okrx2.adapter.ObservableBody
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.genericGsonType
import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.ModelConvert
import com.zh.android.base.http.PageModel
import com.zh.android.circle.mall.enums.OrderByType
import com.zh.android.circle.mall.model.MallGoodsCategoryModel
import com.zh.android.circle.mall.model.MallGoodsModel
import com.zh.android.circle.mall.model.MallIndexInfoModel
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
            val type = genericGsonType<HttpModel<MallIndexInfoModel>>();
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
            val type = genericGsonType<HttpModel<List<MallGoodsCategoryModel>>>();
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
            val type = genericGsonType<HttpModel<PageModel<MallGoodsModel>>>();
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
            val type = genericGsonType<HttpModel<MallGoodsModel>>();
            val request: GetRequest<HttpModel<MallGoodsModel>> =
                OkGo.get(ApiUrl.MALL_GET_GOODS_DETAIL)
            return request.tag(tag)
                .params("goodsId", goodsId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }
    }
}