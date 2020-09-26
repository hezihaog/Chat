package com.hule.dashi.mediaplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

/**
 * <b>Package:</b> com.hule.dashi.answer.util.media <br>
 * <b>Create Date:</b> 2019/2/5  6:47 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 对MediaPlayer做了一些包装 <br>
 */
public class DefaultMediaPlayer extends MediaPlayer {
    private PlayerStatusEnum mStatusEnum;

    public DefaultMediaPlayer() {
        mStatusEnum = PlayerStatusEnum.IDLE;
    }

    @Override
    public void reset() {
        super.reset();
        mStatusEnum = PlayerStatusEnum.IDLE;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        mStatusEnum = PlayerStatusEnum.STARTED;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mStatusEnum = PlayerStatusEnum.STOPPED;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        mStatusEnum = PlayerStatusEnum.PAUSED;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        super.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mStatusEnum = PlayerStatusEnum.COMPLETED;
                if (listener != null) {
                    listener.onCompletion(mp);
                }
            }
        });
    }

    /**
     * 是否是加载中
     */
    public boolean isLoading() {
        return PlayerStatusEnum.INITIALIZED == mStatusEnum;
    }

    /**
     * 是在暂停中
     */
    public boolean isPause() {
        return PlayerStatusEnum.PAUSED == mStatusEnum;
    }

    /**
     * 是否播放完成
     */
    public boolean isComplete() {
        return PlayerStatusEnum.COMPLETED == mStatusEnum;
    }

    @Override
    public void setDataSource(@NonNull Context context, @NonNull Uri uri) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(context, uri);
        setInitializedStatus();
    }

    @Override
    public void setDataSource(@NonNull Context context, @NonNull Uri uri, @Nullable Map<String, String> headers) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(context, uri, headers);
        setInitializedStatus();
    }

    @Override
    public void setDataSource(@NonNull Context context, @NonNull Uri uri, @Nullable Map<String, String> headers, @Nullable List<HttpCookie> cookies) throws IOException {
        super.setDataSource(context, uri, headers, cookies);
        setInitializedStatus();
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
        setInitializedStatus();
    }

    @Override
    public void setDataSource(@NonNull AssetFileDescriptor afd) throws IOException, IllegalArgumentException, IllegalStateException {
        super.setDataSource(afd);
        setInitializedStatus();
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        super.setDataSource(fd);
        setInitializedStatus();
    }

    @Override
    public void setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException {
        super.setDataSource(fd, offset, length);
        setInitializedStatus();
    }

    @Override
    public void setDataSource(MediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException {
        super.setDataSource(dataSource);
        setInitializedStatus();
    }

    private void setInitializedStatus() {
        mStatusEnum = PlayerStatusEnum.INITIALIZED;
    }
}