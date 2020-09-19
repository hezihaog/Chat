package com.zh.android.chat.service.module.moment

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider

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
}