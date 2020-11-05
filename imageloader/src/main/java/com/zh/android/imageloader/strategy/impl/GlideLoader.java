package com.zh.android.imageloader.strategy.impl;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zh.android.imageloader.LoadImageCallback;
import com.zh.android.imageloader.LoadOption;
import com.zh.android.imageloader.strategy.ILoaderStrategy;

import java.io.File;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * <b>Package:</b> com.tongwei.imageloader.strategy.impl <br>
 * <b>Create Date:</b> 2019-08-22  21:21 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class GlideLoader implements ILoaderStrategy {
    private Context mApplicationContext;
    private final RequestOptions mRequestOptions;

    public GlideLoader() {
        mRequestOptions = new RequestOptions()
                .dontAnimate()
                //内存缓存处理图,磁盘缓存原图
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    }

    @Override
    public void init(Context context) {
        mApplicationContext = context.getApplicationContext();
    }

    @Override
    public void load(Context context, LoadOption option, ImageView targetView) {
        //复制一份Options
        RequestOptions requestOptions = mRequestOptions.clone();
        //根据option中的资源类型决定加载的资源类型，再加载加载
        RequestBuilder<?> requestBuilder = adapterMultipleResources(context, option);
        if (requestBuilder == null) {
            //不是能处理的类型，一般不会走到这里
            return;
        }
        //处理圆形
        if (option.isRound()) {
            requestBuilder = requestBuilder.circleCrop();
        }
        //处理圆角
        requestBuilder = requestBuilder
                .apply(RequestOptions.bitmapTransform(new MultiTransformation<>(
                        //左上角圆角
                        new RoundedCornersTransformation((int) option.getTopLeftRadius(), 0, RoundedCornersTransformation.CornerType.TOP_LEFT),
                        //右上角圆角
                        new RoundedCornersTransformation((int) option.getTopRightRadius(), 0, RoundedCornersTransformation.CornerType.TOP_RIGHT),
                        //左下角圆角
                        new RoundedCornersTransformation((int) option.getBottomLeftRadius(), 0, RoundedCornersTransformation.CornerType.BOTTOM_LEFT),
                        //右下角圆角
                        new RoundedCornersTransformation((int) option.getBottomRightRadius(), 0, RoundedCornersTransformation.CornerType.BOTTOM_RIGHT))));
        //处理默认占位图
        int defaultImage = option.getDefaultImgResId();
        if (defaultImage != 0) {
            requestBuilder = requestBuilder
                    .placeholder(defaultImage)
                    .error(defaultImage);
        }
        requestBuilder
                //复用标准配置
                .apply(requestOptions)
                //加载
                .into(targetView);
    }

    @Override
    public void loadToBitmap(Context context, LoadOption option, LoadImageCallback callback) {
        RequestBuilder<Bitmap> requestBuilder = Glide.with(context).asBitmap();
        adapterMultipleResources(requestBuilder, option).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (callback != null) {
                    callback.onSuccess(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (callback != null) {
                    callback.onFail();
                }
            }
        });
    }

    private RequestBuilder<?> adapterMultipleResources(Context context, LoadOption option) {
        RequestManager manager = Glide.with(context);
        return adapterMultipleResources(manager, option);
    }

    private RequestBuilder<?> adapterMultipleResources(RequestManager requestManager, LoadOption option) {
        Class<?> resourceClass = option.getResourceClass();
        Object resource = option.getResource();
        RequestBuilder<?> requestBuilder = null;
        if (String.class.equals(resourceClass)) {
            //处理转义字符
            String url = replaceEscape((String) resource);
            requestBuilder = requestManager.load(url);
        } else if (File.class.equals(resourceClass)) {
            requestBuilder = requestManager.load((File) resource);
        } else if (Integer.class.equals(resourceClass)) {
            requestBuilder = requestManager.load((Integer) resource);
        } else if (Drawable.class.equals(resourceClass)) {
            requestBuilder = requestManager.load((Drawable) resource);
        } else if (Bitmap.class.equals(resourceClass)) {
            requestBuilder = requestManager.load((Bitmap) resource);
        }
        return requestBuilder;
    }

    private <T> RequestBuilder<T> adapterMultipleResources(RequestBuilder<T> requestBuilder, LoadOption option) {
        Class<?> resourceClass = option.getResourceClass();
        Object resource = option.getResource();
        if (String.class.equals(resourceClass)) {
            //处理转义字符
            String url = replaceEscape((String) resource);
            requestBuilder = requestBuilder.load(url);
        } else if (File.class.equals(resourceClass)) {
            requestBuilder = requestBuilder.load((File) resource);
        } else if (Drawable.class.equals(resourceClass)) {
            requestBuilder = requestBuilder.load((Drawable) resource);
        } else if (Bitmap.class.equals(resourceClass)) {
            requestBuilder = requestBuilder.load((Bitmap) resource);
        }
        return requestBuilder;
    }

    @Override
    public void clearMemoryCache(int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(mApplicationContext).clearMemory();
        }
        Glide.get(mApplicationContext).trimMemory(level);
    }

    @Override
    public void clearDiskCache() {
        Thread thread = new Thread(() -> Glide.get(mApplicationContext).clearDiskCache());
        thread.start();
    }

    @Override
    public void pause(Context context) {
        Glide.with(context).pauseRequests();
    }

    @Override
    public void resume(Context context) {
        Glide.with(context).resumeRequests();
    }

    @Override
    public void onLowMemory() {
        Glide.get(mApplicationContext).onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Glide.get(mApplicationContext).onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Glide.get(mApplicationContext).onConfigurationChanged(newConfig);
    }

    /**
     * 替换url中的换行符
     */
    private String replaceEscape(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        return url.replaceAll("\\\\", "");
    }
}