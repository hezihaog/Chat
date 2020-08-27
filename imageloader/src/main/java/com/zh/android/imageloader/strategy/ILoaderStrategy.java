package com.zh.android.imageloader.strategy;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.ImageView;

import com.zh.android.imageloader.LoadImageCallback;
import com.zh.android.imageloader.LoadOption;


/**
 * <b>Package:</b> com.tongwei.imageloader.strategy <br>
 * <b>Create Date:</b> 2019-08-22  21:14 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public interface ILoaderStrategy {
    /**
     * 初始化，在application的onCreate中初始化，该方法存在意义是为了有些框架需要在Application中初始化
     */
    void init(Context context);

    /**
     * 加载图片
     *
     * @param option     加载可选项
     * @param targetView 目标ImageView
     */
    void load(Context context, LoadOption option, ImageView targetView);

    /**
     * 加载图片为Bitmap并回调
     *
     * @param option   加载可选项
     * @param callback 回调
     */
    void loadToBitmap(Context context, LoadOption option, LoadImageCallback callback);

    /**
     * 清除内存缓存
     */
    void clearMemoryCache(int level);

    /**
     * 清除磁盘缓存
     */
    void clearDiskCache();

    /**
     * 暂停请求，一般在ListView或RecyclerView滚动时调用，停止加载快速滚动的图片数据
     */
    void pause(Context context);

    /**
     * 恢复请求，在ListView或RecyclerView滚动停止时调用
     */
    void resume(Context context);

    /**
     * 转调onLowMemory()给Loader处理
     */
    void onLowMemory();

    /**
     * 转调onTrimMemory()给Loader处理
     */
    void onTrimMemory(int level);

    /**
     * 转调onConfigurationChanged()给Loader处理
     */
    void onConfigurationChanged(Configuration newConfig);
}