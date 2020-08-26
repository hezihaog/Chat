package com.zh.android.chat.mine

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.module.mine.MineService

/**
 * @author wally
 * @date 2020/08/26
 */
@Route(path = ARouterUrl.MINE_SERVICE, name = "我的模块服务")
class MineServiceImpl : MineService {
    override fun init(context: Context?) {
    }
}