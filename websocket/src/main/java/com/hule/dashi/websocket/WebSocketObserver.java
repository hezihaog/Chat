package com.hule.dashi.websocket;


import androidx.annotation.NonNull;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * <b>Package:</b> com.hule.dashi.websocket <br>
 * <b>Create Date:</b> 2019/1/17  1:38 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 外部使用者，需要使用该观察者 <br>
 */
public class WebSocketObserver implements Observer<WebSocketInfo> {
    private boolean isConnect;
    private Disposable disposable;

    @Override
    public void onSubscribe(Disposable disposable) {
        this.disposable = disposable;
        onStart(disposable);
    }

    @Override
    public void onNext(WebSocketInfo webSocketInfo) {
        if (webSocketInfo.isConnect()) {
            isConnect = true;
            if (webSocketInfo.getStringMsg() != null) {
                onMessage(webSocketInfo.getStringMsg());
            } else if (webSocketInfo.getByteStringMsg() != null) {
                onMessage(webSocketInfo.getByteStringMsg());
            } else {
                onConnect(webSocketInfo.getWebSocket());
            }
        } else if (webSocketInfo.isPrepareReconnect()) {
            onPrepareReconnect();
        } else if (webSocketInfo.isReconnect()) {
            onReconnect();
        } else if (!webSocketInfo.isConnect()) {
            isConnect = false;
            onClose();
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        if (isConnect) {
            onClose();
        }
    }

    public final void dispose() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void onStart(Disposable disposable) {
    }

    /**
     * 连接成功
     */
    public void onConnect(@NonNull WebSocket webSocket) {
    }

    /**
     * 重连成功
     */
    public void onReconnect() {
    }

    /**
     * 准备重连
     */
    public void onPrepareReconnect() {
    }

    public void onMessage(@NonNull String text) {
    }

    public void onMessage(@NonNull ByteString byteString) {
    }

    public void onClose() {
    }
}