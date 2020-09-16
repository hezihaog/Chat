package com.zh.android.contextprovider;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;


/**
 * Package: com.github.wally.applicationprovider
 * FileName: ContextProvider
 * Date: on 2018/5/31  下午11:08
 * Auther: zihe
 * Descirbe:给外部调用的单例管理器
 * Email: hezihao@linghit.com
 */
public class ContextProvider {
    @SuppressLint("StaticFieldLeak")
    private static volatile ContextProvider instance;
    private Context mContext;
    private Handler mMainHandler;

    private ContextProvider(Context context) {
        mContext = context;
    }

    /**
     * 获取实例
     */
    public static ContextProvider get() {
        if (instance == null) {
            synchronized (ContextProvider.class) {
                if (instance == null) {
                    Context context = ApplicationContextProvider.mContext;
                    if (context == null) {
                        context = getApplicationByReflect();
                    }
                    instance = new ContextProvider(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取上下文
     */
    public Context getContext() {
        return mContext;
    }

    public Application getApplication() {
        return (Application) mContext.getApplicationContext();
    }

    /**
     * 反射ActivityThread获取Context
     */
    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            return (Application) app;
        } catch (NoSuchMethodException | IllegalAccessException
                | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }

    private void ensureInitMainHandler() {
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }
    }

    public void post(Runnable task) {
        ensureInitMainHandler();
        mMainHandler.post(task);
    }

    public void postDelayed(Runnable task, long delayMillis) {
        ensureInitMainHandler();
        mMainHandler.postDelayed(task, delayMillis);
    }
}