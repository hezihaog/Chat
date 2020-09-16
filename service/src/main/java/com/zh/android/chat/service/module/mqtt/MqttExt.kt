package com.zh.android.chat.service.module.mqtt

/**
 * <b>Package:</b> com.tongwei.smarttoilet.service.mqtt <br>
 * <b>Create Date:</b> 2019-09-11  16:55 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */

/**
 * 字符串转换为Mqtt的User类型Topic
 */
fun String.toUserMqttTopic(): String {
    return "/user/${this}"
}