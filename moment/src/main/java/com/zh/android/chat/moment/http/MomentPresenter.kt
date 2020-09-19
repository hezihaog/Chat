package com.zh.android.chat.moment.http

import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.PageModel
import com.zh.android.chat.moment.model.MomentModel
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/09/19
 */
class MomentPresenter {
    companion object {
        private val TAG = MomentPresenter::class.java.simpleName
    }

    /**
     * 获取动态列表
     * @param pageNum 页码
     * @param pageSize 每页多少条
     */
    fun getMomentList(
        pageNum: Int,
        pageSize: Int
    ): Observable<HttpModel<PageModel<MomentModel>>> {
        return MomentRequester.getMomentList(TAG, pageNum, pageSize)
    }
}