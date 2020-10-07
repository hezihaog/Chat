package com.zh.android.chat.notice.http

import com.lzy.okgo.OkGo
import com.lzy.okgo.request.GetRequest
import com.lzy.okgo.request.PostRequest
import com.lzy.okrx2.adapter.ObservableBody
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.genericGsonType
import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.ModelConvert
import com.zh.android.base.http.PageModel
import com.zh.android.chat.notice.model.NoticeModel
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/10/06
 */
class NoticeRequester {
    companion object {
        /**
         * 获取通知列表
         */
        fun getNoticeList(
            tag: String,
            userId: String,
            pageNum: Int,
            pageSize: Int
        ): Observable<HttpModel<PageModel<NoticeModel>>> {
            val type = genericGsonType<HttpModel<PageModel<NoticeModel>>>()
            val request: GetRequest<HttpModel<PageModel<NoticeModel>>> =
                OkGo.get(ApiUrl.GET_NOTICE_LIST)
            return request.tag(tag)
                .params("userId", userId)
                .params("pageNum", pageNum)
                .params("pageSize", pageSize)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 已读一条通知
         */
        fun readNotice(
            tag: String,
            noticeId: String,
            userId: String
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.READ_NOTICE)
            return request.tag(tag)
                .params("noticeId", noticeId)
                .params("userId", userId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 已读所有通知
         */
        fun readAllNotice(
            tag: String,
            userId: String
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.READ_ALL_NOTICE)
            return request.tag(tag)
                .params("userId", userId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }
    }
}