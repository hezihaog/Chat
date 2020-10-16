package com.zh.android.chat.service.module.mall

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author wally
 * @date 2020/10/16
 * 商城模块服务接口
 */
interface MallService : IProvider {
    /**
     * 跳转到商城页面
     */
    fun goMall(activity: Activity)
}