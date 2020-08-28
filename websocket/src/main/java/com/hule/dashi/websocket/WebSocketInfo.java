package com.hule.dashi.websocket;


import com.hule.dashi.websocket.cache.ICacheTarget;

import java.io.Serializable;

import okhttp3.WebSocket;
import okio.ByteString;

/**
 * <b>Package:</b> com.hule.dashi.websocket <br>
 * <b>Create Date:</b> 2019/1/17  11:16 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> WebSocket信息包裹对象 <br>
 */
public class WebSocketInfo implements Serializable, ICacheTarget<WebSocketInfo> {
    private static final long serialVersionUID = -880481254453932113L;

    private WebSocket mWebSocket;
    private String mStringMsg;
    private ByteString mByteStringMsg;
    /**
     * 连接成功
     */
    private boolean isConnect;
    /**
     * 重连成功
     */
    private boolean isReconnect;
    /**
     * 准备重连
     */
    private boolean isPrepareReconnect;

    /**
     * 重置
     */
    @Override
    public WebSocketInfo reset() {
        this.mWebSocket = null;
        this.mStringMsg = null;
        this.mByteStringMsg = null;
        this.isConnect = false;
        this.isReconnect = false;
        this.isPrepareReconnect = false;
        return this;
    }

    public WebSocketInfo setWebSocket(WebSocket webSocket) {
        mWebSocket = webSocket;
        return this;
    }

    public WebSocketInfo setStringMsg(String stringMsg) {
        mStringMsg = stringMsg;
        return this;
    }

    public WebSocketInfo setByteStringMsg(ByteString byteStringMsg) {
        mByteStringMsg = byteStringMsg;
        return this;
    }

    public WebSocketInfo setConnect(boolean connect) {
        this.isConnect = connect;
        return this;
    }

    public WebSocketInfo setReconnect(boolean reconnect) {
        this.isReconnect = reconnect;
        return this;
    }

    public WebSocketInfo setPrepareReconnect(boolean prepareReconnect) {
        this.isPrepareReconnect = prepareReconnect;
        return this;
    }

    public WebSocket getWebSocket() {
        return mWebSocket;
    }

    public String getStringMsg() {
        return mStringMsg;
    }

    public ByteString getByteStringMsg() {
        return mByteStringMsg;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public boolean isReconnect() {
        return isReconnect;
    }

    public boolean isPrepareReconnect() {
        return isPrepareReconnect;
    }
}