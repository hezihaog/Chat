package com.zh.android.chat.service.module.mine

import android.app.Activity
import com.zh.android.base.http.HttpModel
import com.zh.android.chat.service.core.IBaseModuleService
import com.zh.android.chat.service.module.mine.model.User
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/08/26
 */
interface MineService : IBaseModuleService {
    /**
     * 获取我的首页
     */
    fun getMineFragment(): String

    /**
     * 根据用户Id，获取用户信息
     */
    fun getUserInfo(userId: String): Observable<HttpModel<User>>

    /**
     * 跳转到修改头像页面
     * @param avatarUrl 当前的头像url
     */
    fun goModifyAvatar(activity: Activity, userId: String, avatarUrl: String)
}