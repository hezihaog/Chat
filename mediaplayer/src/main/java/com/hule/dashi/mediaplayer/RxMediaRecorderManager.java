package com.hule.dashi.mediaplayer;

import android.content.Context;

import io.reactivex.Observable;

/**
 * <b>Package:</b> com.hule.dashi.mediaplayer <br>
 * <b>Create Date:</b> 2019/2/19  4:12 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 录音管理 <br>
 */
public class RxMediaRecorderManager implements IMediaRecorder {
    private final IMediaRecorder mMediaRecorderImpl;

    public RxMediaRecorderManager(Context context) {
        mMediaRecorderImpl = new MediaRecorderImpl();
        setContext(context);
    }

    @Override
    public void applyMediaOption(RecorderOption recorderOption) {
        mMediaRecorderImpl.applyMediaOption(recorderOption);
    }

    @Override
    public Observable<Boolean> startRecord() {
        return mMediaRecorderImpl.startRecord();
    }

    @Override
    public Observable<Boolean> cancelRecord() {
        return mMediaRecorderImpl.cancelRecord();
    }

    @Override
    public Observable<Boolean> finishRecord() {
        return mMediaRecorderImpl.finishRecord();
    }

    @Override
    public void setContext(Context context) {
        mMediaRecorderImpl.setContext(context);
    }

    @Override
    public Observable<MediaRecorderInfo> subscribeMediaRecorder() {
        return mMediaRecorderImpl.subscribeMediaRecorder();
    }

    @Override
    public boolean deleteTargetVoiceFile(String voiceFilePath) {
        return mMediaRecorderImpl.deleteTargetVoiceFile(voiceFilePath);
    }

    @Override
    public boolean isRecording() {
        return mMediaRecorderImpl.isRecording();
    }
}