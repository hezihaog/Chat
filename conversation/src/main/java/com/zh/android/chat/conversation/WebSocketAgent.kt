package com.zh.android.chat.conversation

import android.content.Context
import com.apkfuns.logutils.LogUtils
import com.hule.dashi.websocket.Logger
import com.hule.dashi.websocket.RxWebSocket
import com.hule.dashi.websocket.RxWebSocketBuilder
import com.lzy.okgo.OkGo
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * @author wally
 * @date 2020/08/28
 * WebSocket连接单例，保存连接
 */
object WebSocketAgent {
    /**
     * WenSocket连接
     */
    private var mRxWebSocket: RxWebSocket? = null

    /**
     * 获取单例的RxWebSocket
     */
    fun getRxWebSocket(context: Context): Observable<RxWebSocket> {
        return Observable.create<RxWebSocket> {
            if (mRxWebSocket != null) {
                it.onNext(mRxWebSocket!!)
            } else {
                mRxWebSocket = RxWebSocketBuilder(context)
                    .isPrintLog(true)
                    .logger(LogDelegate())
                    //5秒无响应则重连
                    .reconnectInterval(5, TimeUnit.SECONDS)
                    .client(OkGo.getInstance().okHttpClient)
                    .build()
                it.onNext(mRxWebSocket!!)
            }
        }.share().doOnDispose {
            mRxWebSocket = null
        }
    }

    /**
     * Log代理
     */
    private class LogDelegate : Logger.LogDelegate {
        override fun i(tag: String?, msg: String?, vararg obj: Any?) {
            LogUtils.i(msg, obj)
        }

        override fun w(tag: String?, msg: String?, vararg obj: Any?) {
            LogUtils.w(msg, obj)
        }

        override fun e(tag: String?, msg: String?, vararg obj: Any?) {
            LogUtils.e(msg, obj)
        }

        override fun d(tag: String?, msg: String?, vararg obj: Any?) {
            LogUtils.d(msg, obj)
        }

        override fun printErrStackTrace(
            tag: String?,
            tr: Throwable?,
            format: String?,
            vararg obj: Any?
        ) {
            tr?.printStackTrace()
        }
    }
}