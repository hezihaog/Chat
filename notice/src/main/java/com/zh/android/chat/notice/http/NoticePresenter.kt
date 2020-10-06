package com.zh.android.chat.notice.http

import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.PageModel
import com.zh.android.chat.notice.model.NoticeModel
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/10/06
 */
class NoticePresenter {
    companion object {
        private val TAG = NoticePresenter::class.java.simpleName
    }

    /**
     * 获取通知列表
     */
    fun getNoticeList(
        userId: String,
        pageNum: Int,
        pageSize: Int
    ): Observable<HttpModel<PageModel<NoticeModel>>> {
        return NoticeRequester.getNoticeList(TAG, userId, pageNum, pageSize)
    }

    /**
     * 获取通知列表
     */
    fun readNotice(
        noticeId: String,
        userId: String
    ): Observable<HttpModel<*>> {
        return NoticeRequester.readNotice(TAG, noticeId, userId)
    }
}