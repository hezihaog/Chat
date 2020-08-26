package com.zh.android.base.util.rx.scheduler;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib.utils <br>
 * <b>FileName:</b> IoMainScheduler <br>
 * <b>Create Date:</b> 2019/1/9  10:55 AM <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class IoMainScheduler<T> extends BaseRxScheduler<T> {
    public IoMainScheduler() {
        super(Schedulers.io(), AndroidSchedulers.mainThread());
    }
}