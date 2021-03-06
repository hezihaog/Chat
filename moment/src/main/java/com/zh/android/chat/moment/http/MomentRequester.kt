package com.zh.android.chat.moment.http

import com.lzy.okgo.OkGo
import com.lzy.okgo.request.GetRequest
import com.lzy.okgo.request.PostRequest
import com.lzy.okrx2.adapter.ObservableBody
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.genericGsonType
import com.zh.android.base.ext.toJson
import com.zh.android.base.ext.useCache
import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.ModelConvert
import com.zh.android.base.http.PageModel
import com.zh.android.chat.moment.enums.MomentReplyType
import com.zh.android.chat.moment.enums.PublicFlag
import com.zh.android.chat.moment.model.*
import io.reactivex.Observable
import java.util.*

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
                .useCache()
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取动态视频列表
         * @param pageNum 页码
         * @param pageSize 每页多少条
         */
        fun getMomentVideoList(
            tag: String,
            userId: String?,
            pageNum: Int,
            pageSize: Int
        ): Observable<HttpModel<PageModel<MomentModel>>> {
            val type = genericGsonType<HttpModel<PageModel<MomentModel>>>()
            val request: GetRequest<HttpModel<PageModel<MomentModel>>> =
                OkGo.get(ApiUrl.GET_MOMENT_LIST_BY_VIDEO_TYPE)
            return request.tag(tag)
                .params("userId", userId)
                .params("pageNum", pageNum)
                .params("pageSize", pageSize)
                .useCache()
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 搜索动态
         *
         * @param keyword 关键字
         * @param pageNum 页码
         * @param pageSize 每页多少条
         * @param publicFlag 是否私密，如果传null，则不限制
         */
        fun searchMoment(
            tag: String,
            userId: String?,
            keyword: String,
            publicFlag: PublicFlag?,
            pageNum: Int,
            pageSize: Int
        ): Observable<HttpModel<PageModel<MomentModel>>> {
            val type = genericGsonType<HttpModel<PageModel<MomentModel>>>()
            val request: GetRequest<HttpModel<PageModel<MomentModel>>> =
                OkGo.get(ApiUrl.SEARCH_MOMENT)
            return request.tag(tag)
                .params("userId", userId)
                .params("keyword", keyword).apply {
                    if (publicFlag != null) {
                        params("publicFlag", publicFlag.code)
                    }
                }
                .params("pageNum", pageNum)
                .params("pageSize", pageSize)
                .useCache()
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取动态详情
         * @param momentId 动态Id
         * @param userId 用户Id
         */
        fun getMomentDetail(
            tag: String,
            momentId: String,
            userId: String?
        ): Observable<HttpModel<MomentModel>> {
            val type = genericGsonType<HttpModel<MomentModel>>()
            val request: GetRequest<HttpModel<MomentModel>> =
                OkGo.get(ApiUrl.GET_MOMENT_DETAIL)
            return request.tag(tag)
                .params("momentId", momentId)
                .params("userId", userId)
                .useCache()
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取我的动态列表
         * @param userId 用户Id
         */
        fun getMyMomentList(
            tag: String,
            userId: String,
            pageNum: Int,
            pageSize: Int
        ): Observable<HttpModel<PageModel<MomentModel>>> {
            val type = genericGsonType<HttpModel<PageModel<MomentModel>>>()
            val request: GetRequest<HttpModel<PageModel<MomentModel>>> =
                OkGo.get(ApiUrl.GET_MY_MOMENT_LIST)
            return request.tag(tag)
                .params("userId", userId)
                .params("pageNum", pageNum)
                .params("pageSize", pageSize)
                .useCache()
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取动态评论列表
         */
        fun getMomentCommentList(
            tag: String,
            momentId: String,
            userId: String,
            pageNum: Int,
            pageSize: Int
        ): Observable<HttpModel<PageModel<MomentCommentModel>>> {
            val type = genericGsonType<HttpModel<PageModel<MomentCommentModel>>>()
            val request: GetRequest<HttpModel<PageModel<MomentCommentModel>>> =
                OkGo.get(ApiUrl.GET_MOMENT_COMMENT_LIST)
            return request.tag(tag)
                .params("momentId", momentId)
                .params("userId", userId)
                .params("pageNum", pageNum)
                .params("pageSize", pageSize)
                .useCache()
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取动态点赞列表
         */
        fun getMomentLikeList(
            tag: String,
            momentId: String,
            pageNum: Int,
            pageSize: Int
        ): Observable<HttpModel<PageModel<MomentLikeRecordModel>>> {
            val type = genericGsonType<HttpModel<PageModel<MomentLikeRecordModel>>>()
            val request: GetRequest<HttpModel<PageModel<MomentLikeRecordModel>>> =
                OkGo.get(ApiUrl.GET_MOMENT_LIKE_LIST)
            return request.tag(tag)
                .params("momentId", momentId)
                .params("pageNum", pageNum)
                .params("pageSize", pageSize)
                .useCache()
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 点赞动态
         * @param momentId 动态Id
         * @param userId 用户Id
         */
        fun likeMoment(
            tag: String,
            momentId: String,
            userId: String
        ): Observable<HttpModel<LikeMomentModel>> {
            val type = genericGsonType<HttpModel<LikeMomentModel>>()
            val request: PostRequest<HttpModel<LikeMomentModel>> =
                OkGo.post(ApiUrl.LIKE_MOMENT)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("momentId", momentId)
                    put("userId", userId)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 取消点赞动态
         * @param momentId 动态Id
         * @param userId 用户Id
         */
        fun removeLikeMoment(
            tag: String,
            momentId: String,
            userId: String
        ): Observable<HttpModel<LikeMomentModel>> {
            val type = genericGsonType<HttpModel<LikeMomentModel>>()
            val request: PostRequest<HttpModel<LikeMomentModel>> =
                OkGo.post(ApiUrl.REMOVE_LIKE_MOMENT)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("momentId", momentId)
                    put("userId", userId)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 发布动态
         * @param userId 用户Id
         * @param content 动态内容
         * @param pictures 图片Url列表
         * @param videos 视频Url列表
         * @param publicFlag 是否私密
         */
        fun publishMoment(
            tag: String,
            userId: String,
            content: String,
            pictures: List<String>,
            videos: List<String>,
            publicFlag: PublicFlag
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.ADD_MOMENT)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("userId", userId)
                    put("content", content)
                    put("pictures", pictures)
                    put("videos", videos)
                    put("publicFlag", publicFlag.code)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 转发动态
         * @param momentId 动态Id
         * @param userId 用户Id
         */
        fun forwardMoment(
            tag: String,
            momentId: String,
            userId: String
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.FORWARD_MOMENT)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("momentId", momentId)
                    put("userId", userId)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取动态转发列表
         */
        fun getMomentForwardList(
            tag: String,
            momentId: String,
            pageNum: Int,
            pageSize: Int
        ): Observable<HttpModel<PageModel<MomentForwardRecordModel>>> {
            val type = genericGsonType<HttpModel<PageModel<MomentForwardRecordModel>>>()
            val request: GetRequest<HttpModel<PageModel<MomentForwardRecordModel>>> =
                OkGo.get(ApiUrl.GET_MOMENT_FORWARD_LIST)
            return request.tag(tag)
                .params("momentId", momentId)
                .params("pageNum", pageNum)
                .params("pageSize", pageSize)
                .useCache()
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 评论动态
         * @param momentId 动态Id
         * @param userId 用户Id
         * @param content 评论内容
         */
        fun addMomentComment(
            tag: String,
            momentId: String,
            userId: String,
            content: String
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.ADD_MOMENT_COMMENT)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("momentId", momentId)
                    put("userId", userId)
                    put("content", content)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 删除一条动态评论
         * @param id 评论Id
         * @param momentId 动态Id
         * @param userId 用户Id
         */
        fun deleteMomentComment(
            tag: String,
            id: String,
            momentId: String,
            userId: String
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.DELETE_MOMENT_COMMENT)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("id", id)
                    put("momentId", momentId)
                    put("userId", userId)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 删除动态
         * @param momentId 动态Id
         * @param userId 用户Id
         */
        fun removeMoment(
            tag: String,
            momentId: String,
            userId: String
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.REMOVE_MOMENT)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("momentId", momentId)
                    put("userId", userId)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取动态评论的回复列表
         * @param momentCommentId 动态评论Id
         * @param userId 用户Id
         */
        fun getMomentCommentReplyList(
            tag: String,
            momentCommentId: String,
            userId: String
        ): Observable<HttpModel<MomentCommentModel>> {
            val type = genericGsonType<HttpModel<MomentCommentModel>>()
            val request: GetRequest<HttpModel<MomentCommentModel>> =
                OkGo.get(ApiUrl.GET_MOMENT_COMMENT_REPLY_LIST)
            return request.tag(tag)
                .params("momentCommentId", momentCommentId)
                .params("userId", userId)
                .useCache()
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
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
            tag: String,
            commentId: String?,
            parentId: String?,
            userId: String,
            replyUserId: String,
            content: String,
            replyType: MomentReplyType
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.ADD_MOMENT_COMMENT_REPLY)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    if (!commentId.isNullOrBlank()) {
                        put("commentId", commentId)
                    }
                    if (!parentId.isNullOrBlank()) {
                        put("parentId", parentId)
                    }
                    put("userId", userId)
                    put("replyUserId", replyUserId)
                    put("content", content)
                    put("type", replyType.code)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 删除一条动态的评论的回复，或者回复的回复
         */
        fun removeMomentCommentReply(
            tag: String,
            id: String,
            userId: String
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.REMOVE_MOMENT_COMMENT_REPLY)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("id", id)
                    put("userId", userId)
                }.toJson())
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 设置动态为公开或私密
         * @param userId 用户Id
         * @param momentId 动态Id
         * @param publicFlag 公开标记
         */
        fun setMomentPublicFlag(
            tag: String,
            userId: String,
            momentId: String,
            publicFlag: PublicFlag
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.SET_MOMENT_PUBLIC_FLAG)
            return request.tag(tag)
                .params("userId", userId)
                .params("momentId", momentId)
                .params("publicFlag", publicFlag.code)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }
    }
}