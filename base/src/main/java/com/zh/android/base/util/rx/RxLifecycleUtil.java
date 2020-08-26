package com.zh.android.base.util.rx;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib.utils <br>
 * <b>Create Date:</b> 2019/1/16  6:46 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 配合uber的AutoDispose封装一下 <br>
 */
public class RxLifecycleUtil {
    /**
     * Rx绑定生命周期，在onDestory
     *
     * @param lifecycleOwner 观察对象，Activity或者Fragment
     */
    public static <T> AutoDisposeConverter<T> bindLifecycle(LifecycleOwner lifecycleOwner) {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner, Lifecycle.Event.ON_DESTROY));
    }
}