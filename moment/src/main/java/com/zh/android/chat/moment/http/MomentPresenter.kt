package com.zh.android.chat.moment.http

import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.PageModel
import com.zh.android.chat.moment.model.LikeMomentModel
import com.zh.android.chat.moment.model.MomentCommentModel
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
     * 获取动态评论列表
     * @param momentId 动态Id
     * @param pageNum 页码
     * @param pageSize 每页多少条
     */
    fun getMomentCommentList(
        momentId: String,
        pageNum: Int,
        pageSize: Int
    ): Observable<HttpModel<PageModel<MomentCommentModel>>> {
        return MomentRequester.getMomentCommentList(TAG, momentId, pageNum, pageSize)
    }

    /**
     * 获取动态点赞列表
     * @param momentId 动态Id
     * @param pageNum 页码
     * @param pageSize 每页多少条
     */
    fun getMomentLikeList(
        momentId: String,
        pageNum: Int,
        pageSize: Int
    ): Observable<HttpModel<PageModel<MomentLikeRecordModel>>> {
        return MomentRequester.getMomentLikeList(TAG, momentId, pageNum, pageSize)
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

    /**
     * 发布动态
     * @param userId 用户Id
     * @param content 动态内容
     * @param pictures 图片Url列表
     */
    fun publishMoment(
        userId: String,
        content: String = "",
        pictures: List<String> = listOf()
    ): Observable<HttpModel<*>> {
        return MomentRequester.publishMoment(TAG, userId, content, pictures)
    }
}