package com.zh.android.chat.service.module.mine

import com.alibaba.android.arouter.facade.template.IProvider
import com.zh.android.base.http.HttpModel
import com.zh.android.chat.service.module.mine.model.User
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/08/26
 */
interface MineService : IProvider {
    /**
     * 获取我的首页
     */
    fun getMineFragment(): String

    /**
     * 根据用户Id，获取用户信息
     */
    fun getUserInfo(userId: String): Observable<HttpModel<User>>
}