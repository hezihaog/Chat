package com.zh.android.mqtt;

/**
 * <b>Package:</b> com.tongwei.mqtt <br>
 * <b>Create Date:</b> 2019-09-05  09:31 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> Mqtt消息质量枚举 <br>
 */
public enum MqttQos {
    /**
     * 最多一次，有可能重复或丢失
     */
    MOST_ONCE(0),
    /**
     * 至少一次，有可能重复
     */
    LEAST_ONE(1),
    /**
     * 只有一次，确保消息只到达一次（用于比较严格的计费系统）
     */
    ONLY_ONE(2);

    private int mCode;

    MqttQos(int code) {
        mCode = code;
    }

    public int getCode() {
        return mCode;
    }

    /**
     * Int映射为枚举值
     */
    public static MqttQos mapping(int qos) {
        MqttQos result = null;
        if (qos == MqttQos.MOST_ONCE.mCode) {
            result = MqttQos.MOST_ONCE;
        } else if (qos == MqttQos.LEAST_ONE.mCode) {
            result = MqttQos.LEAST_ONE;
        } else if (qos == MqttQos.ONLY_ONE.mCode) {
            result = MqttQos.ONLY_ONE;
        } else {
            result = MqttQos.MOST_ONCE;
        }
        return result;
    }
}