package com.zh.android.base.util.monitor.memory.impl;


import com.zh.android.base.util.monitor.memory.IMemorySentry;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <b>Package:</b> com.tongwei.smarttoilet.base.util.monitor.memory <br>
 * <b>Create Date:</b> 2019-09-18  16:38 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 线程池实现的定时内存检测 <br>
 */
public class SentryByExecutorService implements IMemorySentry {
    private ScheduledExecutorService mExecutorService;
    private Callback mCallback;

    @Override
    public void start(long period, float thresholdRatio) {
        if (mExecutorService == null) {
            mExecutorService = Executors.newScheduledThreadPool(1, runnable -> {
                Thread thread = new Thread(runnable);
                thread.setName("MemoryMonitor --- Monitor ---");
                return thread;
            });
            mExecutorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    float ratio = getCurrentMemoryRatio();
                    //占用内存到指定峰值时回调
                    if (ratio >= thresholdRatio) {
                        mCallback.onMemoryAlarm();
                    }
                    //Logger.d("定时检测内存占比: " + ratio);
                }
            }, 0, period, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void stop() {
        if (mExecutorService != null) {
            mExecutorService.shutdown();
        }
    }

    @Override
    public void registerCallback(Callback callback) {
        mCallback = callback;
    }
}