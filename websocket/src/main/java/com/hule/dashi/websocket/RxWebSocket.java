package com.hule.dashi.websocket;

import android.content.Context;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okio.ByteString;

/**
 * <b>Package:</b> com.hule.dashi.websocket <br>
 * <b>Create Date:</b> 2019/1/17  10:52 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> Rx封装WebSocket门面类，只提供Api，隐藏内部实现到WebSocketWorker的实现类上 <br>
 */
public class RxWebSocket implements WebSocketWorker {
    private Context mContext;
    /**
     * 是否打印Log
     */
    private boolean mIsPrintLog;
    /**
     * Log代理对象
     */
    private Logger.LogDelegate mLogDelegate;
    /**
     * 支持外部传入OkHttpClient
     */
    private OkHttpClient mClient;
    /**
     * 支持SSL
     */
    private SSLSocketFactory mSslSocketFactory;
    private X509TrustManager mTrustManager;
    /**
     * 重连间隔时间
     */
    private long mReconnectInterval;
    /**
     * 重连间隔时间的单位
     */
    private TimeUnit mReconnectIntervalTimeUnit;
    /**
     * 具体干活的实现类
     */
    private WebSocketWorker mWorkerImpl;

    private RxWebSocket() {
    }

    RxWebSocket(RxWebSocketBuilder builder) {
        this.mContext = builder.mContext;
        this.mIsPrintLog = builder.mIsPrintLog;
        this.mLogDelegate = builder.mLogDelegate;
        this.mClient = builder.mClient == null ? new OkHttpClient() : builder.mClient;
        this.mSslSocketFactory = builder.mSslSocketFactory;
        this.mTrustManager = builder.mTrustManager;
        this.mReconnectInterval = builder.mReconnectInterval == 0 ? 1 : builder.mReconnectInterval;
        this.mReconnectIntervalTimeUnit = builder.mReconnectIntervalTimeUnit == null ? TimeUnit.SECONDS : builder.mReconnectIntervalTimeUnit;
        setup();
    }

    /**
     * 开始配置
     */
    private void setup() {
        this.mWorkerImpl = new WebSocketWorkerImpl(
                this.mContext,
                this.mIsPrintLog,
                this.mLogDelegate,
                this.mClient,
                this.mSslSocketFactory,
                this.mTrustManager,
                this.mReconnectInterval,
                this.mReconnectIntervalTimeUnit);
    }

    @Override
    public Observable<WebSocketInfo> get(String url) {
        return mWorkerImpl.get(url);
    }

    @Override
    public Observable<WebSocketInfo> get(String url, long timeout, TimeUnit timeUnit) {
        return mWorkerImpl.get(url, timeout, timeUnit);
    }

    @Override
    public Observable<Boolean> send(String url, String msg) {
        return mWorkerImpl.send(url, msg);
    }

    @Override
    public Observable<Boolean> send(String url, ByteString byteString) {
        return mWorkerImpl.send(url, byteString);
    }

    @Override
    public Observable<Boolean> asyncSend(String url, String msg) {
        return mWorkerImpl.asyncSend(url, msg);
    }

    @Override
    public Observable<Boolean> asyncSend(String url, ByteString byteString) {
        return mWorkerImpl.asyncSend(url, byteString);
    }

    @Override
    public Observable<Boolean> close(String url) {
        return mWorkerImpl.close(url);
    }

    @Override
    public boolean closeNow(String url) {
        return mWorkerImpl.closeNow(url);
    }

    @Override
    public Observable<List<Boolean>> closeAll() {
        return mWorkerImpl.closeAll();
    }

    @Override
    public void closeAllNow() {
        mWorkerImpl.closeAllNow();
    }

    @Override
    public Observable<Boolean> heartBeat(String url, int period, TimeUnit unit,
                                         HeartBeatGenerateCallback heartBeatGenerateCallback) {
        return mWorkerImpl.heartBeat(url, period, unit, heartBeatGenerateCallback);
    }
}