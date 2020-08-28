package com.hule.dashi.websocket;

/**
 * <b>Package:</b> com.hule.dashi.websocket <br>
 * <b>Create Date:</b> 2019/1/23  10:32 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 非正常关闭WebSocket异常 <br>
 */
public class ImproperCloseException extends Exception {
    private static final long serialVersionUID = -887228982222599777L;

    public ImproperCloseException() {
    }

    public ImproperCloseException(String message) {
        super(message);
    }
}