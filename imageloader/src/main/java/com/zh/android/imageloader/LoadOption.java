package com.zh.android.imageloader;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.File;

/**
 * <b>Package:</b> com.tongwei.imageloader <br>
 * <b>Create Date:</b> 2019-08-22  21:20 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class LoadOption {
    /**
     * 要加载的资源类型
     */
    private Class<?> resourceClass;
    /**
     * 要加载的资源
     */
    private Object resource;
    /**
     * 默认填充的图片ResId
     */
    private int defaultImgResId;
    /**
     * 是否圆形，一般用于头像
     */
    private boolean isRound;
    /**
     * 是否有圆角
     */
    private boolean isCorner;
    /**
     * 左上角圆角半径
     */
    private float topLeftRadius;
    /**
     * 右上角圆角半径
     */
    private float topRightRadius;
    /**
     * 左下角圆角半径
     */
    private float bottomLeftRadius;
    /**
     * 右下角圆角半径
     */
    private float bottomRightRadius;

    public LoadOption(Builder builder) {
        this.resourceClass = builder.resourceClass;
        this.resource = builder.resource;
        this.defaultImgResId = builder.defaultImgResId;
        this.isRound = builder.isRound;
        this.isCorner = builder.isCorner;
        this.topLeftRadius = builder.topLeftRadius;
        this.topRightRadius = builder.topRightRadius;
        this.bottomLeftRadius = builder.bottomLeftRadius;
        this.bottomRightRadius = builder.bottomRightRadius;
    }

    public Class<?> getResourceClass() {
        return resourceClass;
    }

    public Object getResource() {
        return resource;
    }

    public int getDefaultImgResId() {
        return defaultImgResId;
    }

    public boolean isRound() {
        return isRound;
    }

    public boolean isCorner() {
        return isCorner;
    }

    public float getTopLeftRadius() {
        return topLeftRadius;
    }

    public float getTopRightRadius() {
        return topRightRadius;
    }

    public float getBottomLeftRadius() {
        return bottomLeftRadius;
    }

    public float getBottomRightRadius() {
        return bottomRightRadius;
    }

    public static class Builder {
        private Class<?> resourceClass;
        private Object resource;
        private int defaultImgResId;
        private boolean isRound;
        private boolean isCorner;
        private float topLeftRadius;
        private float topRightRadius;
        private float bottomLeftRadius;
        private float bottomRightRadius;

        /**
         * 加载Url
         */
        public Builder setUrl(String url) {
            resourceClass = String.class;
            resource = url;
            return this;
        }

        /**
         * 加载文件File
         */
        public Builder setFile(File file) {
            resourceClass = File.class;
            resource = file;
            return this;
        }

        /**
         * 加载资源Id数据
         */
        public Builder setDrawableResId(int resId) {
            resourceClass = Integer.class;
            resource = resId;
            return this;
        }

        /**
         * 加载Drawable
         */
        public Builder setDrawable(Drawable drawable) {
            resourceClass = Drawable.class;
            resource = drawable;
            return this;
        }

        /**
         * 加载Bitmap
         */
        public Builder setBitmap(Bitmap bitmap) {
            resourceClass = Bitmap.class;
            resource = bitmap;
            return this;
        }

        /**
         * 设置默认图片资源Id
         *
         * @param defaultImgResId 图片资源Id
         */
        public Builder setDefaultImgResId(int defaultImgResId) {
            this.defaultImgResId = defaultImgResId;
            return this;
        }

        /**
         * 是否圆形
         */
        public Builder setRound() {
            this.isRound = true;
            return this;
        }

        /**
         * 设置圆角（4个角都是圆角）
         */
        public Builder setRadius(float radius) {
            setTopLetRadius(radius);
            setTopRightRadius(radius);
            setBottomLeftRadius(radius);
            setBottomRightRadius(radius);
            return this;
        }

        /**
         * 设置左上角圆角半径
         */
        public Builder setTopLetRadius(float radius) {
            isCorner = true;
            topLeftRadius = radius;
            return this;
        }

        /**
         * 设置右上角的圆角半径
         */
        public Builder setTopRightRadius(float radius) {
            isCorner = true;
            topRightRadius = radius;
            return this;
        }

        /**
         * 设置左下角的圆角半径
         */
        public Builder setBottomLeftRadius(float radius) {
            isCorner = true;
            bottomLeftRadius = radius;
            return this;
        }

        /**
         * 设置左下角的圆角半径
         */
        public Builder setBottomRightRadius(float radius) {
            isCorner = true;
            bottomRightRadius = radius;
            return this;
        }

        public LoadOption build() {
            return new LoadOption(this);
        }
    }
}