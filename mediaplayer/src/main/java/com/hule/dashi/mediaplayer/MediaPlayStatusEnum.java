package com.hule.dashi.mediaplayer;

/**
 * <b>Package:</b> com.hule.dashi.answer.util.media <br>
 * <b>Create Date:</b> 2019/2/5  11:06 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 播放状态枚举 <br>
 */
public enum MediaPlayStatusEnum {
    /**
     * 准备完成
     */
    PREPARED(),
    /**
     * 播放中
     */
    PLAYING(),
    /**
     * 暂停播放
     */
    PAUSE(),
    /**
     * 停止播放
     */
    STOPPED(),
    /**
     * 播放完毕
     */
    COMPLETION()
}