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