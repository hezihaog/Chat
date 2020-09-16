package com.zh.android.chat.service.module.mqtt

import com.zh.android.base.constant.ApiUrl


/**
 * <b>Package:</b> com.tongwei.smarttoilet.service.mqtt <br>
 * <b>Create Date:</b> 2019-10-21  10:28 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> Mqtt模块Api <br>
 */

/**
 * Mqtt端口
 */
val ApiUrl.MQTT_PORT
    get() = "1883"

/**
 * MqttUrl地址
 */
val ApiUrl.MQTT_URL
    get() = "tcp://${MQTT_HOST}:${MQTT_PORT}"

/**
 * Mqtt主机名
 */
val ApiUrl.MQTT_HOST: String
    get() {
        return "192.168.211.131"
    }

/**
 * Mqtt账号
 */
val ApiUrl.MQTT_ACCOUNT: String
    get() {
        return "admin"
    }

/**
 * Mqtt密码
 */
val ApiUrl.MQTT_PASSWORD: String
    get() {
        return "admin"
    }