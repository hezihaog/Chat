package com.hule.dashi.mediaplayer;

import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

/**
 * <b>Package:</b> com.hule.dashi.answer.util.media <br>
 * <b>Create Date:</b> 2019/2/5  7:10 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 媒体请求参数 <br>
 */
public class MediaOption {
    private MediaDataSource mMediaDataSource;
    private Map<String, String> mHeaders;
    private List<HttpCookie> mCookies;
    private long mOffset;
    private long mLength;
    private boolean isLoop;
    private float mLeftVolume;
    private float mRightVolume;

    public MediaOption(Builder builder) {
        this.mMediaDataSource = builder.mMediaDataSource;
        this.mHeaders = builder.mHeaders;
        this.mCookies = builder.mCookies;
        this.mOffset = builder.mOffset;
        this.mLength = builder.mLength;
        this.isLoop = builder.isLoop;
        this.mLeftVolume = builder.mLeftVolume;
        this.mRightVolume = builder.mRightVolume;
    }
    public MediaDataSource getMediaDataSource() {
        return mMediaDataSource;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public List<HttpCookie> getCookies() {
        return mCookies;
    }

    public long getOffset() {
        return mOffset;
    }

    public long getLength() {
        return mLength;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public float getLeftVolume() {
        return mLeftVolume;
    }

    public float getRightVolume() {
        return mRightVolume;
    }

    public static class Builder {
        /**
         * 数据源
         */
        private MediaDataSource mMediaDataSource;
        /**
         * 头部
         */
        private Map<String, String> mHeaders;
        /**
         * Cookie
         */
        private List<HttpCookie> mCookies;
        private long mOffset;
        private long mLength;
        /**
         * 是否循环
         */
        private boolean isLoop;
        /**
         * 左声道声量
         */
        private float mLeftVolume = 1f;
        /**
         * 右声道声量
         */
        private float mRightVolume = 1f;

        public Builder(MediaDataSource mediaDataSource) {
            mMediaDataSource = mediaDataSource;
        }

        public Builder setHeaders(Map<String, String> headers) {
            mHeaders = headers;
            return this;
        }

        public Builder setCookies(List<HttpCookie> cookies) {
            mCookies = cookies;
            return this;
        }

        public Builder setOffset(long offset) {
            mOffset = offset;
            return this;
        }

        public Builder setLength(long length) {
            mLength = length;
            return this;
        }

        public Builder setLoop(boolean loop) {
            isLoop = loop;
            return this;
        }

        public Builder setLeftVolume(float leftVolume) {
            mLeftVolume = leftVolume;
            return this;
        }

        public Builder setRightVolume(float rightVolume) {
            mRightVolume = rightVolume;
            return this;
        }

        public MediaOption build() {
            return new MediaOption(this);
        }
    }
}