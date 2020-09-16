package com.zh.android.base.util.monitor.memory;

/**
 * <b>Package:</b> com.tongwei.smarttoilet.base.util.monitor.memory <br>
 * <b>Create Date:</b> 2019-09-18  16:32 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 监听接口 <br>
 */
public interface IMemorySentry {
    /**
     * 开启检测
     *
     * @param period         每次检查之间的间隔时间
     * @param thresholdRatio 内存占比峰值
     */
    void start(long period, float thresholdRatio);

    /**
     * 停止检测
     */
    void stop();

    /**
     * 注册回调
     */
    void registerCallback(Callback callback);

    /**
     * 获取当前内存占比
     */
    default float getCurrentMemoryRatio() {
        //当前已申请总的内存
        long totalMemory = Runtime.getRuntime().totalMemory();
        //app已耗内存：总内存 - 可用内存
        long usedMemory = totalMemory - Runtime.getRuntime().freeMemory();
        //最大内存总量
        long maxMemory = Runtime.getRuntime().maxMemory();
        //当前占用内存占比
        return (1.0f * usedMemory) / (maxMemory * 1.0f);
    }

    interface Callback {
        /**
         * 内存告警回调
         */
        void onMemoryAlarm();
    }
}