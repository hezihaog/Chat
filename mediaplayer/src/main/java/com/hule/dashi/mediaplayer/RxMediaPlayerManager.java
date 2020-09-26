package com.hule.dashi.mediaplayer;

import android.content.Context;

import io.reactivex.Observable;

/**
 * <b>Package:</b> com.hule.dashi.answer.util.media <br>
 * <b>Create Date:</b> 2019/2/5  7:24 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 管理器 <br>
 */
public class RxMediaPlayerManager implements IMediaPlayer {
    private IMediaPlayer mMediaPlayerImpl;

    public RxMediaPlayerManager(Context context) {
        mMediaPlayerImpl = new MediaPlayerImpl();
        setContext(context.getApplicationContext());
    }

    @Override
    public Observable<Boolean> start() {
        return mMediaPlayerImpl.start();
    }

    @Override
    public Observable<Boolean> stop() {
        return mMediaPlayerImpl.stop();
    }

    @Override
    public Observable<Boolean> pause() {
        return mMediaPlayerImpl.pause();
    }

    @Override
    public Observable<Boolean> resume() {
        return mMediaPlayerImpl.resume();
    }

    @Override
    public Observable<Boolean> reset() {
        return mMediaPlayerImpl.reset();
    }

    @Override
    public Observable<Boolean> release() {
        return mMediaPlayerImpl.release();
    }

    @Override
    public void seekTo(int progress) {
        mMediaPlayerImpl.seekTo(progress);
    }


    @Override
    public void applyMediaOption(MediaOption mediaOption) {
        mMediaPlayerImpl.applyMediaOption(mediaOption);
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        mMediaPlayerImpl.setVolume(leftVolume, rightVolume);
    }

    @Override
    public void setContext(Context context) {
        mMediaPlayerImpl.setContext(context);
    }

    @Override
    public void setOnBufferingUpdateListener(MediaPlayerListener.OnBufferingUpdateListener onBufferingUpdateListener) {
        mMediaPlayerImpl.setOnBufferingUpdateListener(onBufferingUpdateListener);
    }

    @Override
    public void setOnSeekCompleteListener(MediaPlayerListener.OnSeekCompleteListener onSeekCompleteListener) {
        mMediaPlayerImpl.setOnSeekCompleteListener(onSeekCompleteListener);
    }

    @Override
    public Observable<MediaPlayInfo> subscribeMediaPlayer() {
        return mMediaPlayerImpl.subscribeMediaPlayer();
    }

    @Override
    public void setOnProgressUpdateListener(MediaPlayerListener.OnProgressUpdateListener listener) {
        mMediaPlayerImpl.setOnProgressUpdateListener(listener);
    }

    @Override
    public boolean isPrepared() {
        return mMediaPlayerImpl.isPrepared();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayerImpl.isPlaying();
    }

    @Override
    public boolean isPause() {
        return mMediaPlayerImpl.isPause();
    }

    @Override
    public MediaOption getApplyMediaOption() {
        return mMediaPlayerImpl.getApplyMediaOption();
    }
}