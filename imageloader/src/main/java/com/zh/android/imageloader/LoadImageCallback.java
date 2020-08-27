package com.zh.android.imageloader;

import android.graphics.Bitmap;

/**
 * <b>Package:</b> com.tongwei.imageloader <br>
 * <b>Create Date:</b> 2019-09-18  10:18 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 加载图片回调 <br>
 */
public interface LoadImageCallback {
    /**
     * 加载成功
     *
     * @param bitmap 图片Bitmap对象
     */
    void onSuccess(Bitmap bitmap);

    /**
     * 加载失败
     */
    void onFail();
}