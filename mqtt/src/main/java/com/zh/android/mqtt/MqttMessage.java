package com.zh.android.mqtt;

/**
 * <b>Package:</b> com.tongwei.mqtt <br>
 * <b>Create Date:</b> 2019-09-04  15:41 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> QMTT消息 <br>
 */
public class MqttMessage {
    /**
     * 订阅的主题
     */
    private String topic;
    /**
     * 接收到的消息
     */
    private String message;
    /**
     * 是否是保留消息，粘性，默认为false，除非是发送方设置为true才会为true
     */
    private boolean retained;
    /**
     * 接收质量
     */
    private MqttQos qos;

    public MqttMessage(String topic, String message, boolean retained, MqttQos qos) {
        this.topic = topic;
        this.message = message;
        this.retained = retained;
        this.qos = qos;
    }

    public String getTopic() {
        return topic;
    }

    public MqttMessage setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public MqttMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isRetained() {
        return retained;
    }

    public MqttMessage setRetained(boolean retained) {
        this.retained = retained;
        return this;
    }

    public MqttQos getQos() {
        return qos;
    }

    public MqttMessage setQos(MqttQos qos) {
        this.qos = qos;
        return this;
    }
}