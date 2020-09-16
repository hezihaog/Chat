package com.zh.android.base.util.monitor.memory.impl;

import android.os.Handler;
import android.os.HandlerThread;

import com.zh.android.base.util.monitor.memory.IMemorySentry;


/**
 * <b>Package:</b> com.tongwei.smarttoilet.base.util.monitor.memory <br>
 * <b>Create Date:</b> 2019-09-18  16:43 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> HandleThread实现的内存定时检测 <br>
 */
public class SentryByHandleThread implements IMemorySentry {
    private HandlerThread mMonitorHandlerThread;
    private Callback mCallback;

    @Override
    public void start(long period, float thresholdRatio) {
        mMonitorHandlerThread = new HandlerThread("memory_monitor_handler_thread");
        mMonitorHandlerThread.start();
        Handler monitorHandler = new Handler(mMonitorHandlerThread.getLooper());
        monitorHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                float ratio = getCurrentMemoryRatio();
                //占用内存到指定峰值时回调
                if (ratio >= thresholdRatio) {
                    mCallback.onMemoryAlarm();
                }
                //Logger.d("定时检测内存占比: " + ratio);
                //继续循环
                monitorHandler.postDelayed(this, period);
            }
        }, period);
    }

    @Override
    public void stop() {
        if (mMonitorHandlerThread != null) {
            mMonitorHandlerThread.quit();
        }
    }

    @Override
    public void registerCallback(Callback callback) {
        mCallback = callback;
    }
}