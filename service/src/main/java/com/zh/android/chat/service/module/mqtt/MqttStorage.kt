package com.zh.android.chat.service.module.mqtt

import com.blankj.utilcode.util.SPUtils
import java.util.*

/**
 * <b>Package:</b> com.tongwei.smarttoilet.service.mqtt <br>
 * <b>Create Date:</b> 2019-09-12  10:21 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> Mqtt共性存储 <br>
 */
object MqttStorage {
    private const val MODULE_KEY_NAME = "toilet_mqtt"

    /**
     * 保存Username对应的Mqtt-ClientId
     */
    private const val KEY_MQTT_CLIENT_ID = "mqtt_client_id"

    /**
     * 保存指定用户的Mqtt-ClientId
     */
    @JvmStatic
    fun saveMqttClientId(username: String, clientId: String) {
        SPUtils.getInstance().put(genMqttClientIdKey(username), clientId)
    }

    /**
     * 获取获取指定用户的Mqtt-ClientId
     */
    @JvmStatic
    fun getMqttClientId(username: String): String {
        return SPUtils.getInstance().getString(
            genMqttClientIdKey(username),
            "${username}-${(Random().nextInt() * 10000)}"
        )
    }

    /**
     * 用username生成唯一的MqttClientId的Key
     */
    private fun genMqttClientIdKey(username: String): String {
        return "${KEY_MQTT_CLIENT_ID}-${username}"
    }
}