package com.zh.android.chat.discovery

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.module.discovery.DiscoveryService

/**
 * @author wally
 * @date 2020/08/26
 */
@Route(path = ARouterUrl.DISCOVERY_SERVICE, name = "发现模块服务")
class DiscoveryServiceImpl : DiscoveryService {
    override fun init(context: Context?) {
    }
}