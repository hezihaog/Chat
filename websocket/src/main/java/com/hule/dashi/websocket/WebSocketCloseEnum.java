package com.hule.dashi.websocket;

/**
 * <b>Package:</b> com.hule.dashi.websocket <br>
 * <b>Create Date:</b> 2019/1/23  10:27 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> WebSocket退出理由和code <br>
 */
public enum WebSocketCloseEnum {
    /**
     * 用户正常退出
     */
    USER_EXIT(3000, "用户正常退出");

    private int mCode;
    private String mReason;

    WebSocketCloseEnum(int code, String reason) {
        mCode = code;
        mReason = reason;
    }

    public int getCode() {
        return mCode;
    }

    public String getReason() {
        return mReason;
    }

    @Override
    public String toString() {
        return "WebSocketCloseEnum{" +
                "mCode=" + mCode +
                ", mReason='" + mReason + '\'' +
                '}';
    }
}