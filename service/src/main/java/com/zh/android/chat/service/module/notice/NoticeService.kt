package com.zh.android.chat.service.module.notice

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author wally
 * @date 2020/10/06
 * 通知模块接口
 */
interface NoticeService : IProvider {
    /**
     * 跳转到通知页面
     */
    fun goNotice(activity: Activity)
}