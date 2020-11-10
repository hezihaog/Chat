package com.zh.android.chat.service.module.moment

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider
import com.zh.android.chat.service.module.moment.enums.MomentPublishType

/**
 * @author wally
 * @date 2020/09/19
 * 动态模块服务接口
 */
interface MomentService : IProvider {
    /**
     * 跳转到动态列表
     */
    fun goMomentList(activity: Activity)

    /**
     * 跳转到我的动态列表
     */
    fun goMyMomentList(activity: Activity)

    /**
     * 跳转到只有视频的动态
     */
    fun goMomentVideoList(activity: Activity)

    /**
     * 跳转到动态搜索
     * @param isMyMoment 是否是我的动态页面跳转过去的
     */
    fun goMomentSearch(activity: Activity, isMyMoment: Boolean)

    /**
     * 跳转到动态详情
     * @param momentId 动态Id
     */
    fun goMomentDetail(activity: Activity, momentId: String);

    /**
     * 跳转到发布动态
     * @param type 发布类型
     */
    fun goMomentPublish(activity: Activity, type: MomentPublishType)

    /**
     * 跳转到动态评论详情
     * @param momentId 动态Id
     * @param momentCommentId 动态评论Id
     */
    fun goMomentCommentDetail(
        activity: Activity,
        momentId: String,
        momentCommentId: String
    )
}