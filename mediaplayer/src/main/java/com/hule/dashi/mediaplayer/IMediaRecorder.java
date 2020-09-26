package com.hule.dashi.mediaplayer;

import android.content.Context;

import io.reactivex.Observable;

/**
 * <b>Package:</b> com.hule.dashi.mediaplayer <br>
 * <b>Create Date:</b> 2019/2/19  4:02 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 语音录制器接口 <br>
 */
public interface IMediaRecorder {
    /**
     * 应用配置
     */
    void applyMediaOption(RecorderOption recorderOption);

    /**
     * 开始录制
     */
    Observable<Boolean> startRecord();

    /**
     * 取消录制
     */
    Observable<Boolean> cancelRecord();

    /**
     * 结束录制
     */
    Observable<Boolean> finishRecord();

    /**
     * 保存Context
     */
    void setContext(Context context);

    /**
     * 订阅录音状态信息
     */
    Observable<MediaRecorderInfo> subscribeMediaRecorder();

    /**
     * 删除目标录音文件
     *
     * @param voiceFilePath 录音文件路径
     * @return 是否删除成功
     */
    boolean deleteTargetVoiceFile(String voiceFilePath);

    /**
     * 是否正在录音
     *
     * @return true为正在录音，false为不是
     */
    boolean isRecording();
}