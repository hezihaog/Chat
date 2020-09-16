package com.zh.android.mqtt;

import io.reactivex.Observable;

/**
 * <b>Package:</b> com.tongwei.mqtt <br>
 * <b>Create Date:</b> 2019-09-04  14:56 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> MQTT对外接口 <br>
 */
public interface MqttApi {
    /**
     * 连接服务器
     *
     * @param option 配置
     */
    Observable<Boolean> connectServer(MqttOption option);

    /**
     * 断开连接
     */
    Observable<Boolean> disconnectServer();

    /**
     * 重启连接
     */
    Observable<Boolean> restartConnectServer();

    /**
     * 发布消息
     *
     * @param topic   主题
     * @param message 消息文本
     */
    Observable<Boolean> publishMessage(String topic, String message);

    /**
     * 监听消息接收
     */
    Observable<MqttMessage> subscribeMessage();

    /**
     * 订阅连接状态
     */
    Observable<ConnectionStatus> subscribeConnectionStatus();

    /**
     * 订阅消息发送状态
     */
    Observable<MessagePublishStatus> subscribeMessagePublishStatus();

    /**
     * 动态订阅一个主题
     *
     * @param topic 主题
     */
    Observable<Boolean> subscribeTopic(String... topic);

    /**
     * 动态取消订阅一个主题
     *
     * @param topic 主题
     */
    Observable<Boolean> unsubscribeTopic(String... topic);
}