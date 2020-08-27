package com.zh.android.chat.mine.http

import com.lzy.okgo.OkGo
import com.lzy.okgo.request.GetRequest
import com.lzy.okrx2.adapter.ObservableBody
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.genericGsonType
import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.ModelConvert
import com.zh.android.chat.service.module.mine.model.User
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/08/27
 * 我的模块请求封装
 */
class MineRequester {
    companion object {
        /**
         * 根据用户Id获取用户信息
         * @param userId 用户Id
         */
        fun getUserInfo(
            tag: String,
            userId: String
        ): Observable<HttpModel<User>> {
            val type = genericGsonType<HttpModel<User>>()
            val request: GetRequest<HttpModel<User>> = OkGo.get(ApiUrl.MINE_GET_USER_INFO)
            return request.tag(tag)
                .params("userid", userId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }
    }
}