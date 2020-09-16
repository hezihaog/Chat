package com.zh.android.mqtt;

/**
 * <b>Package:</b> com.tongwei.mqtt <br>
 * <b>Create Date:</b> 2019-09-04  18:40 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> Mqtt不正常关闭异常 <br>
 */
public class MqttImproperCloseException extends Exception {
    private static final long serialVersionUID = -4030414538155742302L;

    MqttImproperCloseException() {
    }

    public MqttImproperCloseException(String message) {
        super(message);
    }
}