package com.hule.dashi.websocket;

/**
 * <b>Package:</b> com.hule.dashi.websocket <br>
 * <b>Create Date:</b> 2019/3/6  5:41 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 心跳生成回调 <br>
 */
public interface HeartBeatGenerateCallback {
    /**
     * 当需要生成心跳信息时回调
     *
     * @param timestamp 当前时间戳
     * @return 要发送的心跳信息
     */
    String onGenerateHeartBeatMsg(long timestamp);
}