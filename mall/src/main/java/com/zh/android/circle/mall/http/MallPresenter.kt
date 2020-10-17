package com.zh.android.circle.mall.http

import com.zh.android.base.http.HttpModel
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
}