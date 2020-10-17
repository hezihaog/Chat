package com.zh.android.circle.mall.http

import com.lzy.okgo.OkGo
import com.lzy.okgo.request.GetRequest
import com.lzy.okrx2.adapter.ObservableBody
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.genericGsonType
import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.ModelConvert
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
    }
}