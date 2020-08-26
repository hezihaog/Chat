package com.zh.android.chat.service.module.mine

import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author wally
 * @date 2020/08/26
 */
interface MineService : IProvider {
    /**
     * 获取我的首页
     */
    fun getMineFragment(): String
}