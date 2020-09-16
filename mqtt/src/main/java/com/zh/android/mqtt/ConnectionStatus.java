package com.zh.android.mqtt;

/**
 * <b>Package:</b> com.tongwei.mqtt <br>
 * <b>Create Date:</b> 2019-09-05  10:38 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 连接状态 <br>
 */
public class ConnectionStatus {
    /**
     * 是否丢失连接
     */
    private boolean isLost;
    /**
     * 是否在重新连接
     */
    private boolean isRetry;
    /**
     * 丢失连接时的异常
     */
    private Throwable error;

    public ConnectionStatus(boolean isLost, Throwable error) {
        this.isLost = isLost;
        this.error = error;
    }

    public ConnectionStatus(boolean isRetry) {
        this.isRetry = isRetry;
    }

    public boolean isLost() {
        return isLost;
    }

    public void setLost(boolean lost) {
        isLost = lost;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public boolean isRetry() {
        return isRetry;
    }
}