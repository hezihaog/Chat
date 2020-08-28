package com.hule.dashi.websocket;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * <b>Package:</b> com.hule.dashi.websocket <br>
 * <b>Create Date:</b> 2019/1/17  10:53 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class RxWebSocketBuilder {
    Context mContext;
    /**
     * 是否打印Log
     */
    boolean mIsPrintLog;
    /**
     * Log代理对象
     */
    Logger.LogDelegate mLogDelegate;
    /**
     * 支持外部传入OkHttpClient
     */
    OkHttpClient mClient;
    /**
     * 支持SSL
     */
    SSLSocketFactory mSslSocketFactory;
    X509TrustManager mTrustManager;
    /**
     * 重连间隔时间
     */
    long mReconnectInterval;
    /**
     * 重连间隔时间的单位
     */
    TimeUnit mReconnectIntervalTimeUnit;

    public RxWebSocketBuilder(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public RxWebSocketBuilder isPrintLog(boolean isPrintLog) {
        this.mIsPrintLog = isPrintLog;
        return this;
    }

    public RxWebSocketBuilder logger(Logger.LogDelegate logDelegate) {
        Logger.setDelegate(logDelegate);
        return this;
    }

    public RxWebSocketBuilder client(OkHttpClient client) {
        this.mClient = client;
        return this;
    }

    public RxWebSocketBuilder sslSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
        this.mSslSocketFactory = sslSocketFactory;
        this.mTrustManager = trustManager;
        return this;
    }

    public RxWebSocketBuilder reconnectInterval(long reconnectInterval, TimeUnit reconnectIntervalTimeUnit) {
        this.mReconnectInterval = reconnectInterval;
        this.mReconnectIntervalTimeUnit = reconnectIntervalTimeUnit;
        return this;
    }

    public RxWebSocket build() {
        return new RxWebSocket(this);
    }
}