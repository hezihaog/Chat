package com.hule.dashi.mediaplayer;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * <b>Package:</b> com.hule.dashi.answer.util.media <br>
 * <b>Create Date:</b> 2019/2/5  9:35 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class MediaPlayObserver implements Observer<MediaPlayInfo> {
    private Disposable mDisposable;

    @Override
    public void onSubscribe(Disposable disposable) {
        mDisposable = disposable;
        onStart(disposable);
    }

    @Override
    public void onNext(MediaPlayInfo info) {
        if (isIntercept(info)) {
            return;
        }
        MediaOption applyMediaOption = info.getApplyMediaOption();
        if (info.isPrepared()) {
            onPrepared(applyMediaOption);
        } else if (info.isPlaying()) {
            onPlaying(applyMediaOption);
        } else if (info.isPause()) {
            onPause(applyMediaOption);
        } else if (info.isStopped()) {
            onStopped(applyMediaOption);
        } else if (info.isCompletion()) {
            onCompletion(applyMediaOption);
        } else if (info.isUpdateProgress()) {
            onUpdateProgress(info);
        }
    }

    /**
     * 是否拦截
     */
    public boolean isIntercept(MediaPlayInfo info) {
        return false;
    }

    public void onPrepared(MediaOption applyMediaOption) {
    }

    public void onPlaying(MediaOption applyMediaOption) {
    }

    public void onPause(MediaOption applyMediaOption) {
    }

    public void onStopped(MediaOption applyMediaOption) {
    }

    public void onCompletion(MediaOption applyMediaOption) {
    }


    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
    }

    public void onStart(Disposable disposable) {
    }

    public final void dispose() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    public void onUpdateProgress(MediaPlayInfo info){}
}