package com.hule.dashi.mediaplayer;

import android.annotation.TargetApi;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;

import java.io.FileDescriptor;

/**
 * <b>Package:</b> com.hule.dashi.answer.util.media <br>
 * <b>Create Date:</b> 2019/2/5  6:56 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 媒体数据源 <br>
 */
public class MediaDataSource {
    /**
     * 播放资源Id
     */
    private String mVoiceId = "";
    /**
     * 播放资源
     */
    private RealSource<?> mRealSource;

    //---------- 下面重载方法都是为了规范数据源类型 ----------

    public MediaDataSource(Uri uri) {
        setRealSource(new RealSource<>(uri));
    }

    public MediaDataSource(String path) {
        setRealSource(new RealSource<>(path));
    }

    @TargetApi(24)
    public MediaDataSource(String voiceId, AssetFileDescriptor fileDescriptor) {
        mVoiceId = voiceId;
        setRealSource(new RealSource<>(fileDescriptor));
    }

    public MediaDataSource(String voiceId, FileDescriptor fileDescriptor) {
        mVoiceId = voiceId;
        setRealSource(new RealSource<>(fileDescriptor));
    }

    public String getVoiceId() {
        return mVoiceId;
    }

    private <T> void setRealSource(RealSource<T> realSource) {
        this.mRealSource = realSource;
    }

    public RealSource<?> getRealSource() {
        return mRealSource;
    }

    /**
     * 真正的数据源数据包装到这个类中
     *
     * @param <T>泛型则是真正的数据源类型
     */
    public static class RealSource<T> {
        private T mSource;

        public RealSource(T source) {
            mSource = source;
        }

        public T getSource() {
            return mSource;
        }

        public void setSource(T source) {
            mSource = source;
        }
    }
}