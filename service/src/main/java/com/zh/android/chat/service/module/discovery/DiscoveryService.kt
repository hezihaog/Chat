package com.zh.android.chat.service.module.discovery

import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author wally
 * @date 2020/08/26
 */
interface DiscoveryService : IProvider {
    /**
     * 获取发现首页Fragment
     */
    fun getDiscoveryFragment(): String
}