package com.zh.android.chat.service.module.base.interceptor

import okhttp3.Request

/**
 * Package: me.wally.lvjiguide.http
 * FileName: IRequestProcessHandler
 * Date: on 2018/9/28  上午10:14
 * Auther: zihe
 * Descirbe:
 * Email: hezihao@linghit.com
 */
interface RequestProcessHandler {
    /**
     * 是否可以处理
     * @return 返回true代表可以处理，返回false代表不可以处理
     */
    fun isCanHandle(originRequest: Request): Boolean

    /**
     * 处理
     * @param originRequest 原始的请求
     * @return 处理过后的请求
     */
    fun process(originRequest: Request): Request
}