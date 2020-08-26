package com.zh.android.base.core;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

/**
 * <b>Package:</b> com.linghit.base.core <br>
 * <b>Create Date:</b> 2019-07-09  16:42 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> LifecycleOwner拓展接口 <br>
 */
public interface LifecycleOwnerExt extends LifecycleOwner {
    /**
     * 获取Activity
     */
    FragmentActivity getActivity();
}