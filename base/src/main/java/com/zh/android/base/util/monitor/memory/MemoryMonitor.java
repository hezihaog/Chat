package com.zh.android.base.util.monitor.memory;

import android.os.Handler;
import android.os.Looper;


import com.zh.android.base.util.monitor.memory.impl.SentryByHandleThread;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <b>Package:</b> com.tongwei.smarttoilet.base.util.monitor <br>
 * <b>Create Date:</b> 2019-09-09  09:34 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 内存检测 <br>
 */
public class MemoryMonitor {
    private CopyOnWriteArrayList<Callback> mCallbacks;
    /**
     * 监听器具体实现
     */
    private IMemorySentry mWatchDog;
    /**
     * 主线程Handler，用来分发内存到峰值后，子线程回调时，转为主线程
     */
    private final Handler mMainHandler;

    private MemoryMonitor() {
        mCallbacks = new CopyOnWriteArrayList<>();
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    private static final class SingleHolder {
        private static final MemoryMonitor INSTANCE = new MemoryMonitor();
    }

    public static MemoryMonitor getInstance() {
        return SingleHolder.INSTANCE;
    }

    /**
     * 开启检测
     *
     * @param period         每次检查之间的间隔时间
     * @param thresholdRatio 内存占比峰值
     */
    public void start(long period, float thresholdRatio) {
        if (mWatchDog == null) {
            mWatchDog = new SentryByHandleThread();
        }
        mWatchDog.start(period, thresholdRatio);
        mWatchDog.registerCallback(new IMemorySentry.Callback() {
            @Override
            public void onMemoryAlarm() {
                if (mCallbacks.isEmpty()) {
                    return;
                }
                mMainHandler.post(() -> {
                    for (MemoryMonitor.Callback callback : mCallbacks) {
                        callback.onLowMemory();
                    }
                });
            }
        });
    }

    /**
     * 停止检测
     */
    public void stop() {
        if (mWatchDog != null) {
            mWatchDog.stop();
        }
    }

    /**
     * 回调
     */
    public interface Callback {
        /**
         * 到达指定的内存峰值
         */
        void onLowMemory();
    }

    public void registerCallback(Callback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    public void unregisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }
}