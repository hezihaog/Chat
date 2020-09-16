package com.zh.android.chat.conversation.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.apkfuns.logutils.LogUtils
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.ioToMain
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getConversationService
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.conversation.model.Message
import com.zh.android.chat.service.module.mqtt.*
import com.zh.android.mqtt.MqttOption
import com.zh.android.mqtt.MqttProxy
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

/**
 * <b>Package:</b> com.tongwei.smarttoilet.notice.service <br>
 * <b>Create Date:</b> 2019-09-05  13:39 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 通知模块的Mqtt服务 <br>
 */
class ConversationMqttService : Service() {
    private val mDisposables: CompositeDisposable = CompositeDisposable()

    /**
     * 退出登录广播监听器
     */
    private lateinit var mLogoutReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        mLogoutReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                mDisposables.run {
                    val proxy = MqttProxy.getInstance()
                    add(
                        //先取消订阅Topic，再断开连接
                        proxy.unsubscribeTopic(*getTopic())
                            .doOnNext {
                                LogUtils.d("Mqtt用户退出登录，取消订阅主题成功: $it")
                            }
                            .flatMap {
                                if (it) {
                                    proxy.disconnectServer()
                                } else {
                                    Observable.just(false)
                                }
                            }
                            .subscribe({ result ->
                                if (result) {
                                    LogUtils.d("Mqtt用户退出登录，断开连接成功")
                                } else {
                                    LogUtils.d("Mqtt用户退出登录，断开连接失败")
                                }
                            }, { error ->
                                error.printStackTrace()
                                LogUtils.d("Mqtt用户退出登录，断开连接异常：${error.message}")
                            })
                    )
                }
            }
        }
        //退出登录
        AppBroadcastManager.register(mLogoutReceiver, AppConstant.Action.LOGIN_USER_LOGOUT)
        LogUtils.d("-------------------------- MqttService启动 --------------------------")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        connectMqttService()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppBroadcastManager.unregister(mLogoutReceiver)
        if (!mDisposables.isDisposed) {
            mDisposables.clear()
        }
        LogUtils.d("-------------------------- MqttService销毁 --------------------------")
    }

    /**
     * 连接服务端Mqtt
     */
    private fun connectMqttService() {
        val userId = getLoginService()?.getUserId() ?: ""
        val mqttClientId = MqttStorage.getMqttClientId(userId)
            .apply {
                LogUtils.d("Mqtt-ClientId: $this")
                MqttStorage.saveMqttClientId(userId, this)
            }
        mDisposables.apply {
            MqttProxy.getInstance().apply {
                val topics = getTopic()
                //连接服务
                add(
                    connectServer(
                        MqttOption.Builder()
                            .setServerUrl(ApiUrl.MQTT_URL)
                            .setUsername(ApiUrl.MQTT_ACCOUNT)
                            .setPassWord(ApiUrl.MQTT_PASSWORD)
                            .setClientId(
                                //获取保存的ClientId，每个账号登录后都生成一个并保存
                                mqttClientId
                            )
                            .setTopics(topics)
                            .build()
                    ).ioToMain().subscribe({ isConnectSuccess ->
                        LogUtils.d("Mqtt连接是否成功: $isConnectSuccess")
                    }, { error ->
                        error.printStackTrace()
                        LogUtils.d("Mqtt连接失败，原因: ${error.message}")
                    })
                )
                //订阅消息接收
                add(
                    subscribeMessage()
                        .ioToMain()
                        .compose(ConversationMessageParser.parseMqttMessage(object :
                            MqttMessageReceiver {
                            override fun onReceiveOfflineChatMsg(model: Message) {
                                //接收到离线聊天消息
                                LogUtils.d("接收到离线聊天消息：$model")
                                model.chatRecord?.let {
                                    getConversationService()?.sendOfflineChatMessageNotification(
                                        model
                                    )
                                }
                            }
                        }))
                        .subscribe({ mqttMessage ->
                            val msg =
                                "Mqtt收到消息: ${mqttMessage.message}, 是否为保留消息：${mqttMessage.isRetained}，接收质量：${mqttMessage.qos}"
                            LogUtils.d(msg)
                        }, { error ->
                            error.printStackTrace()
                            LogUtils.d("Mqtt收到消息，但是抛出异常，原因: ${error.message}")
                        })
                )
                //订阅连接状态
                add(
                    subscribeConnectionStatus()
                        .ioToMain()
                        .subscribe({ status ->
                            if (status.isLost) {
                                LogUtils.d("Mqtt连接丢失，原因: ${status.error.message}")
                            } else if (status.isRetry) {
                                LogUtils.d("Mqtt重试连接中...")
                            }
                        }, { error ->
                            error.printStackTrace()
                        })
                )
                //订阅消息发送状态
                add(
                    subscribeMessagePublishStatus()
                        .ioToMain()
                        .subscribe({ status ->
                            if (status.isComplete) {
                                LogUtils.d("Mqtt消息发送完毕，消息内容: ${status.message}")
                            }
                        }, { error ->
                            error.printStackTrace()
                        })
                )
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return Binder()
    }

    private fun getTopic(): Array<String> {
        return mutableListOf<String>().apply {
            val userId = getLoginService()?.getUserId()
            if (userId != null) {
                add(userId.toUserMqttTopic())
            }
        }.toTypedArray()
    }
}