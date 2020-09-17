package com.zh.android.chat.service.module.base.interceptor

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.CopyOnWriteArrayList

/**
 * <b>Package:</b> com.tongwei.smarttoilet.service.base.interceptor <br>
 * <b>Create Date:</b> 2019-09-04  10:11 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 请求处理器 <br>
 */
class RequestProcessor private constructor() {
    /**
     * 是否启用，默认启用
     */
    private var isEnable = true
    /**
     * 统一分发的拦截器
     */
    private var mDispatchInterceptor: Interceptor
    /**
     * 处理器链
     */
    private val mProcessHandlerChain by lazy {
        CopyOnWriteArrayList<RequestProcessHandler>()
    }

    companion object {
        private class SingleHolder {
            companion object {
                val INSTANCE: RequestProcessor = RequestProcessor()
            }
        }

        fun getInstance(): RequestProcessor {
            return SingleHolder.INSTANCE
        }
    }

    init {
        //注册多种请求处理器
        registerProcessHandler(GetRequestHandler())
        registerProcessHandler(FormPostRequestHandler())
        registerProcessHandler(JsonPostRequestHandler())
        //初始化分发拦截器
        this.mDispatchInterceptor = Interceptor { chain ->
            if (!isEnable) {
                chain.proceed(chain.request())
            } else {
                chain.proceed(processRequest(chain.request()))
            }
        }
    }

    /**
     * 处理请求
     */
    private fun processRequest(originRequest: Request?): Request {
        var outRequest: Request = originRequest!!
        //责任链分派给不同的请求处理器
        mProcessHandlerChain.forEach { handler ->
            //可以处理，就给对应的处理器处理
            if (handler.isCanHandle(originRequest)) {
                outRequest = handler.process(originRequest)
                return@forEach
            }
        }
        return outRequest
    }

    /**
     * 提供给外部传入OkHttpClient的Builder，我们添加完拦截器后返回
     */
    fun with(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        return builder.addInterceptor(mDispatchInterceptor)
    }

    /**
     * 是否启用
     */
    fun isEnable(): Boolean {
        return this.isEnable
    }

    /**
     * 设置是否启动
     */
    fun setEnable(enable: Boolean) {
        this.isEnable = enable
    }

    /**
     * 注册请求处理器
     */
    fun registerProcessHandler(handler: RequestProcessHandler) {
        if (!mProcessHandlerChain.contains(handler)) {
            mProcessHandlerChain.add(handler)
        }
    }

    /**
     * 解注册请求处理器
     */
    fun unregisterProcessHandler(handler: RequestProcessHandler) {
        mProcessHandlerChain.remove(handler)
    }
}