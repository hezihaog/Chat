package com.hule.dashi.mediaplayer;

import android.content.Context;

import io.reactivex.Observable;

/**
 * <b>Package:</b> com.hule.dashi.answer.util.media <br>
 * <b>Create Date:</b> 2019/2/5  6:25 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 多媒体播放器接口 <br>
 */
public interface IMediaPlayer {
    /**
     * 开始播放
     */
    Observable<Boolean> start();

    /**
     * 停止播放
     */
    Observable<Boolean> stop();

    /**
     * 暂停播放
     */
    Observable<Boolean> pause();

    /**
     * 继续播放
     */
    Observable<Boolean> resume();

    /**
     * 重置
     */
    Observable<Boolean> reset();

    /**
     * 回收
     */
    Observable<Boolean> release();

    /**
     * 拖动进度条
     */
    void seekTo(int progress);

    /**
     * 设置进度更新回调
     *
     * @param listener 要设置的进度更新监听器
     */
    void setOnProgressUpdateListener(MediaPlayerListener.OnProgressUpdateListener listener);

    /**
     * 应用媒体配置
     */
    void applyMediaOption(MediaOption mediaOption);

    /**
     * 设置声音大小，取值从0f~1f
     *
     * @param leftVolume  左声道
     * @param rightVolume 右声道
     */
    void setVolume(float leftVolume, float rightVolume);

    /**
     * 保存Context
     */
    void setContext(Context context);

    /**
     * Url网络资源播放，缓存进度监听
     */
    void setOnBufferingUpdateListener(MediaPlayerListener.OnBufferingUpdateListener onBufferingUpdateListener);

    /**
     * 拖动播放进度完成的监听
     */
    void setOnSeekCompleteListener(MediaPlayerListener.OnSeekCompleteListener onSeekCompleteListener);

    /**
     * 监听播放状态
     */
    Observable<MediaPlayInfo> subscribeMediaPlayer();

    /**
     * 是否正在准备
     */
    boolean isPrepared();

    /**
     * 是否正在播放
     */
    boolean isPlaying();

    /**
     * 是否正在暂停
     */
    boolean isPause();

    /**
     * 获取当前应用的配置信息
     */
    MediaOption getApplyMediaOption();
}