package com.zh.android.chat.moment.http

import com.lzy.okgo.OkGo
import com.lzy.okgo.request.GetRequest
import com.lzy.okrx2.adapter.ObservableBody
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.genericGsonType
import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.ModelConvert
import com.zh.android.base.http.PageModel
import com.zh.android.chat.moment.model.MomentModel
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/09/19
 * 动态模块请求封装
 */
class MomentRequester {
    companion object {
        /**
         * 获取动态列表
         * @param pageNum 页码
         * @param pageSize 每页多少条
         */
        fun getMomentList(
            tag: String,
            userId: String?,
            pageNum: Int,
            pageSize: Int
        ): Observable<HttpModel<PageModel<MomentModel>>> {
            val type = genericGsonType<HttpModel<PageModel<MomentModel>>>()
            val request: GetRequest<HttpModel<PageModel<MomentModel>>> =
                OkGo.get(ApiUrl.GET_MOMENT_LIST)
            return request.tag(tag)
                .params("userId", userId)
                .params("pageNum", pageNum)
                .params("pageSize", pageSize)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }
    }
}