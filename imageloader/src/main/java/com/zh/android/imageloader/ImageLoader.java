package com.zh.android.imageloader;

import android.annotation.SuppressLint;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;

import com.zh.android.imageloader.strategy.ILoaderStrategy;
import com.zh.android.imageloader.strategy.impl.GlideLoader;


/**
 * <b>Package:</b> com.tongwei.imageloader <br>
 * <b>Create Date:</b> 2019-08-22  21:14 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class ImageLoader {
    @SuppressLint("StaticFieldLeak")
    private static ImageLoader instance;
    private ILoaderStrategy mLoader;
    private Context mContext;

    public static ImageLoader get(Context context) {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader()
                            .holdContext(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private ImageLoader holdContext(Context context) {
        mContext = context;
        return this;
    }

    public Context getContext() {
        return mContext;
    }

    public void setLoader(ILoaderStrategy loader) {
        mLoader = loader;
        mLoader.init(mContext);
        //注册一个内存低和配置改变监听
        mContext.getApplicationContext().registerComponentCallbacks(new ComponentCallbacks2() {
            @Override
            public void onTrimMemory(int level) {
                loader.onTrimMemory(level);
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                loader.onConfigurationChanged(newConfig);
            }

            @Override
            public void onLowMemory() {
                loader.onLowMemory();
            }
        });
    }

    public ILoaderStrategy getLoader() {
        if (mLoader == null) {
            setLoader(new GlideLoader());
        }
        return mLoader;
    }
}