package com.zh.android.circle.mall.http

import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.PageModel
import com.zh.android.circle.mall.enums.OrderByType
import com.zh.android.circle.mall.model.MallGoodsCategoryModel
import com.zh.android.circle.mall.model.MallGoodsModel
import com.zh.android.circle.mall.model.MallIndexInfoModel
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
}