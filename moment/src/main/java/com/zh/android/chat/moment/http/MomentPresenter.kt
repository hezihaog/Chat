package com.zh.android.chat.moment.http

import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.PageModel
import com.zh.android.chat.moment.model.LikeMomentModel
import com.zh.android.chat.moment.model.MomentLikeRecordModel
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
        userId: String?,
        pageNum: Int,
        pageSize: Int
    ): Observable<HttpModel<PageModel<MomentModel>>> {
        return MomentRequester.getMomentList(TAG, userId, pageNum, pageSize)
    }

    /**
     * 获取动态详情
     * @param momentId 动态Id
     * @param userId 用户Id
     */
    fun getMomentDetail(
        momentId: String,
        userId: String?
    ): Observable<HttpModel<MomentModel>> {
        return MomentRequester.getMomentDetail(TAG, momentId, userId)
    }

    /**
     * 获取动态点赞列表
     */
    fun getMomentLikeList(
        momentId: String
    ): Observable<HttpModel<PageModel<MomentLikeRecordModel>>> {
        return MomentRequester.getMomentLikeList(TAG, momentId)
    }

    /**
     * 点赞动态
     * @param momentId 动态Id
     * @param userId 用户Id
     */
    fun likeMoment(
        momentId: String,
        userId: String
    ): Observable<HttpModel<LikeMomentModel>> {
        return MomentRequester.likeMoment(TAG, momentId, userId)
    }

    /**
     * 取消点赞动态
     * @param momentId 动态Id
     * @param userId 用户Id
     */
    fun removeLikeMoment(
        momentId: String,
        userId: String
    ): Observable<HttpModel<LikeMomentModel>> {
        return MomentRequester.removeLikeMoment(TAG, momentId, userId)
    }
}