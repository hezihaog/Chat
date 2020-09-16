package com.zh.android.mqtt;

/**
 * <b>Package:</b> com.tongwei.mqtt <br>
 * <b>Create Date:</b> 2019-09-05  10:39 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 消息发送状态 <br>
 */
public class MessagePublishStatus {
    /**
     * 是否发送完毕
     */
    private boolean isComplete;
    /**
     * 发送的消息内容
     */
    private String message;

    public MessagePublishStatus(boolean isComplete, String message) {
        this.isComplete = isComplete;
        this.message = message;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}