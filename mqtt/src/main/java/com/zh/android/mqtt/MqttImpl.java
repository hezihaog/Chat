package com.zh.android.mqtt;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * <b>Package:</b> com.tongwei.mqtt <br>
 * <b>Create Date:</b> 2019-09-04  15:46 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> Mqtt实现 <br>
 */
public class MqttImpl implements MqttApi {
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 当前引用的配置
     */
    private MqttOption mCurrentApplyOption;
    /**
     * Mqtt客户端
     */
    private MqttAndroidClient mMqttClient;
    /**
     * 是否已连接
     */
    private boolean isConnect;
    /**
     * 连接状态监听
     */
    private OnConnectionStatusListener mConnectionStatusListener;
    /**
     * 消息发布状态监听器
     */
    private OnMessagePublishStatusListener mMessagePublishStatusListener;

    MqttImpl(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public Observable<Boolean> connectServer(MqttOption option) {
        mCurrentApplyOption = option;
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                if (mMqttClient == null) {
                    mMqttClient = new MqttAndroidClient(
                            mContext,
                            option.getServerUrl(),
                            option.getClientId(),
                            new MemoryPersistence());
                }
                if (isConnect) {
                    emitter.onNext(true);
                    return;
                }
                //如果没有网络，不开启连接
                if (!NetworkUtil.hasNetWorkStatus(mContext, false)) {
                    emitter.onNext(false);
                    return;
                }
                //进行链接配置
                MqttConnectOptions connectOptions = new MqttConnectOptions();
                //如果为false(flag=0)，Client断开连接后，Server应该保存Client的订阅信息
                //如果为true(flag=1)，表示Server应该立刻丢弃任何会话状态信息
                connectOptions.setCleanSession(true);
                //设置用户名和密码
                connectOptions.setUserName(option.getUsername());
                connectOptions.setPassword(option.getPassWord().toCharArray());
                //设置连接超时时间
                connectOptions.setConnectionTimeout(option.getConnectionTimeout());
                //设置心跳发送间隔时间，单位秒
                connectOptions.setKeepAliveInterval(option.getKeepAliveInterval());
                //设置遗嘱
                connectOptions.setWill("android-mqtt-offline-topic", "android-mqtt-is_offline".getBytes(), MqttQos.ONLY_ONE.getCode(), false);
                //不设置自动重连，我们自己手动重试
                connectOptions.setAutomaticReconnect(false);
                //开始连接
                mMqttClient.connect(connectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        emitter.onNext(true);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                        emitter.onNext(false);
                    }
                });
            }
        }).flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
            @Override
            public ObservableSource<Boolean> apply(Boolean isSuccess) throws Exception {
                //连接成功，
                if (isSuccess) {
                    return subscribeTopic(mCurrentApplyOption.getTopics());
                } else {
                    //连接失败，重试
                    return Observable.error(new MqttImproperCloseException());
                }
            }
        }).doOnNext(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean isSuccess) throws Exception {
                isConnect = isSuccess;
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        mMqttClient = null;
                        //所有异常都走重试，延时指定秒值进行重试
                        return Observable.timer(mCurrentApplyOption.getRetryIntervalTime(), TimeUnit.SECONDS)
                                .doOnNext(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        if (mConnectionStatusListener != null) {
                                            mConnectionStatusListener.onRetryConnection();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    @Override
    public Observable<Boolean> disconnectServer() {
        //已经断开连接了
        if (!isConnect) {
            return Observable.just(true);
        }
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                try {
                    mMqttClient.disconnect(null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            mMqttClient = null;
                            emitter.onNext(true);
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            exception.printStackTrace();
                            emitter.onNext(false);
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                    emitter.onNext(false);
                }
            }
        }).doOnNext(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean isSuccess) throws Exception {
                isConnect = !isSuccess;
            }
        });
    }

    @Override
    public Observable<Boolean> restartConnectServer() {
        //先断开，再重新连接
        return disconnectServer().flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
            @Override
            public ObservableSource<Boolean> apply(Boolean isSuccess) throws Exception {
                if (isSuccess) {
                    return connectServer(mCurrentApplyOption);
                }
                return Observable.just(false);
            }
        });
    }

    @Override
    public Observable<Boolean> publishMessage(String topic, String message) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                //连接未打开
                if (!isConnect) {
                    emitter.onNext(false);
                }
                try {
                    //发布消息
                    mMqttClient.publish(topic, message.getBytes(), MqttQos.ONLY_ONE.getCode(), false);
                    emitter.onNext(true);
                } catch (MqttException e) {
                    e.printStackTrace();
                    emitter.onNext(false);
                }
            }
        });
    }

    @Override
    public Observable<MqttMessage> subscribeMessage() {
        return Observable.create(new ObservableOnSubscribe<MqttMessage>() {
            @Override
            public void subscribe(ObservableEmitter<MqttMessage> emitter) throws Exception {
                mMqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        //与服务器之间的连接失效
                        isConnect = false;
                        if (mConnectionStatusListener != null) {
                            mConnectionStatusListener.onConnectionLost(cause);
                        }
                        emitter.onError(cause);
                    }

                    @Override
                    public void messageArrived(String topic, org.eclipse.paho.client.mqttv3.MqttMessage message) throws Exception {
                        //接收到消息
                        emitter.onNext(new MqttMessage(topic, message.toString(),
                                message.isRetained(), MqttQos.mapping(message.getQos())));
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        //发送完成
                        if (mMessagePublishStatusListener != null) {
                            try {
                                String message = token.getMessage().toString();
                                mMessagePublishStatusListener.onMessagePublishComplete(message);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                mMqttClient.setCallback(null);
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        //延时重连
                        return Observable.timer(mCurrentApplyOption.getRetryIntervalTime(), TimeUnit.SECONDS).flatMap(new Function<Long, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Long aLong) throws Exception {
                                return connectServer(mCurrentApplyOption);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public Observable<ConnectionStatus> subscribeConnectionStatus() {
        return Observable.create(new ObservableOnSubscribe<ConnectionStatus>() {
            @Override
            public void subscribe(ObservableEmitter<ConnectionStatus> emitter) throws Exception {
                setOnConnectionStatusListener(new OnConnectionStatusListener() {
                    @Override
                    public void onConnectionLost(Throwable error) {
                        String errorMsg;
                        if (error != null) {
                            errorMsg = error.getMessage();
                        } else {
                            errorMsg = "";
                            error = new RuntimeException(errorMsg);
                        }
                        emitter.onNext(new ConnectionStatus(true, new RuntimeException(errorMsg, error)));
                    }

                    @Override
                    public void onRetryConnection() {
                        emitter.onNext(new ConnectionStatus(true));
                    }
                });
            }
        });
    }

    @Override
    public Observable<MessagePublishStatus> subscribeMessagePublishStatus() {
        return Observable.create(new ObservableOnSubscribe<MessagePublishStatus>() {
            @Override
            public void subscribe(ObservableEmitter<MessagePublishStatus> emitter) throws Exception {
                setOnMessagePublishStatusListener((message)
                        -> emitter.onNext(new MessagePublishStatus(true, message)));
            }
        });
    }

    @Override
    public Observable<Boolean> subscribeTopic(String... topic) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                if (topic.length == 0) {
                    emitter.onNext(true);
                    return;
                }
                try {
                    int size = topic.length;
                    int[] qosArr = new int[size];
                    for (int i = 0; i < size; i++) {
                        qosArr[i] = MqttQos.ONLY_ONE.getCode();
                    }
                    mMqttClient.subscribe(topic, qosArr);
                    emitter.onNext(true);
                } catch (MqttException e) {
                    e.printStackTrace();
                    emitter.onNext(false);
                }
            }
        });
    }

    @Override
    public Observable<Boolean> unsubscribeTopic(String... topic) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                try {
                    mMqttClient.unsubscribe(topic, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            emitter.onNext(true);
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            emitter.onNext(false);
                            if (exception != null) {
                                exception.printStackTrace();
                            }
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                    emitter.onNext(false);
                }
            }
        });
    }

    /**
     * 连接状态监听
     */
    public interface OnConnectionStatusListener {
        /**
         * 连接丢失
         *
         * @param error 异常对象
         */
        void onConnectionLost(Throwable error);

        /**
         * 正在重试连接
         */
        void onRetryConnection();
    }

    private void setOnConnectionStatusListener(OnConnectionStatusListener connectionStatusListener) {
        mConnectionStatusListener = connectionStatusListener;
    }

    /**
     * 消息发送状态监听
     */
    public interface OnMessagePublishStatusListener {
        /**
         * 发送消息完成
         *
         * @param message 原始消息
         */
        void onMessagePublishComplete(String message);
    }

    private void setOnMessagePublishStatusListener(OnMessagePublishStatusListener messagePublishStatusListener) {
        mMessagePublishStatusListener = messagePublishStatusListener;
    }
}