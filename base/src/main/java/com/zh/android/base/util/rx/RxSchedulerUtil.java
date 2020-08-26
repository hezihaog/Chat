package com.zh.android.base.util.rx;


import com.zh.android.base.util.rx.scheduler.BaseRxScheduler;
import com.zh.android.base.util.rx.scheduler.IoMainScheduler;

/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib.utils <br>
 * <b>FileName:</b> RxSchedulerUtil <br>
 * <b>Create Date:</b> 2019/1/9  10:56 AM <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class RxSchedulerUtil {
    private RxSchedulerUtil() {
    }

    /**
     * 子线程转主线程
     */
    public static  <T> BaseRxScheduler<T> ioToMain() {
        //Android线程调度器
        return new IoMainScheduler<>();
    }
}