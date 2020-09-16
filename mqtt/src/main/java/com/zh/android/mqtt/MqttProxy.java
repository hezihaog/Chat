package com.zh.android.mqtt;


import com.zh.android.contextprovider.ContextProvider;

import io.reactivex.Observable;

/**
 * <b>Package:</b> com.tongwei.mqtt <br>
 * <b>Create Date:</b> 2019-09-04  14:56 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> MQTT代理 <br>
 */
public class MqttProxy implements MqttApi {
    private final MqttApi mImpl;

    private MqttProxy() {
        mImpl = new MqttImpl(ContextProvider.get().getContext());
    }

    private static final class SingleHolder {
        private static final MqttProxy INSTANCE = new MqttProxy();
    }

    public static MqttProxy getInstance() {
        return SingleHolder.INSTANCE;
    }

    @Override
    public Observable<Boolean> connectServer(MqttOption option) {
        return mImpl.connectServer(option);
    }

    @Override
    public Observable<Boolean> disconnectServer() {
        return mImpl.disconnectServer();
    }

    @Override
    public Observable<Boolean> restartConnectServer() {
        return mImpl.restartConnectServer();
    }

    @Override
    public Observable<Boolean> publishMessage(String topic, String message) {
        return mImpl.publishMessage(topic, message);
    }

    @Override
    public Observable<MqttMessage> subscribeMessage() {
        return mImpl.subscribeMessage();
    }

    @Override
    public Observable<ConnectionStatus> subscribeConnectionStatus() {
        return mImpl.subscribeConnectionStatus();
    }

    @Override
    public Observable<MessagePublishStatus> subscribeMessagePublishStatus() {
        return mImpl.subscribeMessagePublishStatus();
    }

    @Override
    public Observable<Boolean> subscribeTopic(String... topic) {
        return mImpl.subscribeTopic(topic);
    }

    @Override
    public Observable<Boolean> unsubscribeTopic(String... topic) {
        return mImpl.unsubscribeTopic(topic);
    }
}