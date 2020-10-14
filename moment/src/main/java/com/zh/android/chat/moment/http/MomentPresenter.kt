package com.zh.android.chat.moment.http

import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.PageModel
import com.zh.android.chat.moment.enums.MomentReplyType
import com.zh.android.chat.moment.model.*
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
     * 获取动态视频列表
     * @param pageNum 页码
     * @param pageSize 每页多少条
     */
    fun getMomentVideoList(
        userId: String?,
        pageNum: Int,
        pageSize: Int
    ): Observable<HttpModel<PageModel<MomentModel>>> {
        return MomentRequester.getMomentVideoList(TAG, userId, pageNum, pageSize)
    }

    /**
     * 搜索动态
     * @param keyword 关键字
     * @param pageNum 页码
     * @param pageSize 每页多少条
     */
    fun searchMoment(
        userId: String?,
        keyword: String,
        pageNum: Int,
        pageSize: Int
    ): Observable<HttpModel<PageModel<MomentModel>>> {
        return MomentRequester.searchMoment(TAG, userId, keyword, pageNum, pageSize)
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
        userId: String,
        pageNum: Int,
        pageSize: Int
    ): Observable<HttpModel<PageModel<MomentCommentModel>>> {
        return MomentRequester.getMomentCommentList(TAG, momentId, userId, pageNum, pageSize)
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
     * @param videos 视频Url列表
     */
    fun publishMoment(
        userId: String,
        content: String = "",
        pictures: List<String> = listOf(),
        videos: List<String> = listOf()
    ): Observable<HttpModel<*>> {
        return MomentRequester.publishMoment(TAG, userId, content, pictures, videos)
    }

    /**
     * 转发动态
     * @param momentId 动态Id
     * @param userId 用户Id
     */
    fun forwardMoment(
        momentId: String,
        userId: String
    ): Observable<HttpModel<*>> {
        return MomentRequester.forwardMoment(TAG, momentId, userId)
    }

    /**
     * 获取动态转发列表
     * @param momentId 动态Id
     * @param pageNum 页码
     * @param pageSize 每页多少条
     */
    fun getMomentForwardList(
        momentId: String,
        pageNum: Int,
        pageSize: Int
    ): Observable<HttpModel<PageModel<MomentForwardRecordModel>>> {
        return MomentRequester.getMomentForwardList(TAG, momentId, pageNum, pageSize)
    }

    /**
     * 评论动态
     * @param momentId 动态Id
     * @param userId 用户Id
     * @param content 评论内容
     */
    fun addMomentComment(
        momentId: String,
        userId: String,
        content: String
    ): Observable<HttpModel<*>> {
        return MomentRequester.addMomentComment(TAG, momentId, userId, content)
    }

    /**
     * 删除一条动态评论
     * @param id 评论Id
     * @param momentId 动态Id
     * @param userId 用户Id
     */
    fun deleteMomentComment(
        id: String,
        momentId: String,
        userId: String
    ): Observable<HttpModel<*>> {
        return MomentRequester.deleteMomentComment(TAG, id, momentId, userId)
    }

    /**
     * 删除动态
     * @param momentId 动态Id
     * @param userId 用户Id
     */
    fun removeMoment(
        momentId: String,
        userId: String
    ): Observable<HttpModel<*>> {
        return MomentRequester.removeMoment(TAG, momentId, userId)
    }

    /**
     * 获取动态评论的回复列表
     * @param momentCommentId 动态评论Id
     */
    fun getMomentCommentReplyList(
        momentCommentId: String,
        userId: String
    ): Observable<HttpModel<MomentCommentModel>> {
        return MomentRequester.getMomentCommentReplyList(TAG, momentCommentId, userId)
    }

    /**
     * 增加一条动态的评论的回复，或者回复的回复
     * @param commentId 回复的评论的Id，如果是回复的回复，该值设置为null，parentId设置为上一层回复的Id
     * @param parentId 回复的回复的Id，如果是评论的回复，该值设置为null，commentId设置为评论的Id
     * @param userId 要发表回复的用户Id
     * @param replyUserId 被回复的人的用户Id
     * @param content 回复内容
     * @param replyType 1为评论的回复，2为回复的回复
     */
    fun addMomentCommentReply(
        commentId: String?,
        parentId: String?,
        userId: String,
        replyUserId: String,
        content: String,
        replyType: MomentReplyType
    ): Observable<HttpModel<*>> {
        return MomentRequester.addMomentCommentReply(
            TAG,
            commentId,
            parentId,
            userId,
            replyUserId,
            content,
            replyType
        )
    }

    /**
     * 删除一条动态的评论的回复，或者回复的回复
     */
    fun removeMomentCommentReply(
        id: String,
        userId: String
    ): Observable<HttpModel<*>> {
        return MomentRequester.removeMomentCommentReply(TAG, id, userId)
    }
}