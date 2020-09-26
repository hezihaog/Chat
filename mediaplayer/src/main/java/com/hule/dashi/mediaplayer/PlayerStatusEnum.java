package com.hule.dashi.mediaplayer;

/**
 * <b>Package:</b> com.hule.dashi.answer.util.media <br>
 * <b>Create Date:</b> 2019/2/5  6:23 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 多媒体播放器状态 <br>
 */
public enum PlayerStatusEnum {
    /**
     * 闲置
     */
    IDLE,
    /**
     * 初始化中
     */
    INITIALIZED,
    /**
     * 开始
     */
    STARTED,
    /**
     * 暂停
     */
    PAUSED,
    /**
     * 停止
     */
    STOPPED,
    /**
     * 完毕
     */
    COMPLETED
}