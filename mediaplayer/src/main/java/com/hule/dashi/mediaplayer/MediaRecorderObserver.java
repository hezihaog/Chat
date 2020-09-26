package com.hule.dashi.mediaplayer;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * <b>Package:</b> com.hule.dashi.mediaplayer <br>
 * <b>Create Date:</b> 2019/2/19  7:59 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class MediaRecorderObserver implements Observer<MediaRecorderInfo> {
    private Disposable mDisposable;

    @Override
    public void onSubscribe(Disposable disposable) {
        mDisposable = disposable;
        onStart(disposable);
    }

    @Override
    public void onNext(MediaRecorderInfo info) {
        if (info.isRecordDurationShort()) {
            onRecordDurationShort();
        } else if (info.isPrepared()) {
            onPrepared();
        } else if (info.isRecording()) {
            onRecording();
        } else if (info.isFinish()) {
            onFinish(info.getVoiceId(), info.getAudioFilePath(), info.getAudioDuration(), info.isCancel());
        } else if (info.isError()) {
            onError(info.getError());
        }
    }

    @Override
    public void onError(Throwable throwable) {
    }

    @Override
    public void onComplete() {
    }

    public void onStart(Disposable disposable) {
    }

    public void onRecordDurationShort() {
    }

    public void onPrepared() {
    }

    public void onRecording() {
    }

    public void onFinish(String voiceId, String audioFilePath, int audioDuration, boolean isCancel) {
    }

    public final void dispose() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
