package com.zh.android.base.util;

import android.content.BroadcastReceiver;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib.utils <br>
 * <b>Create Date:</b> 2019/3/14  8:36 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 广播注册表，提供LifecycleOwner和BroadcastReceiver即可自动注册广播接收器 <br>
 */
public class BroadcastRegistry {
    private LifecycleOwner mLifecycleOwner;

    public BroadcastRegistry(LifecycleOwner lifecycleOwner) {
        mLifecycleOwner = lifecycleOwner;
    }

    public BroadcastRegistry register(BroadcastReceiver receiver, String... actions) {
        RegistryTableItem tableItem = new RegistryTableItem(receiver, actions);
        mLifecycleOwner.getLifecycle().addObserver(tableItem);
        return this;
    }

    /**
     * 广播注册表项，每一项负责一个广播的注册和注销
     */
    protected static class RegistryTableItem implements LifecycleObserver {
        /**
         * 广播接收器
         */
        private BroadcastReceiver mReceiver;
        /**
         * 广播接收器监听的多个Action
         */
        private String[] mActions;

        RegistryTableItem(BroadcastReceiver receiver, String[] actions) {
            mReceiver = receiver;
            mActions = actions;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        protected void onLifecycleCreate() {
            AppBroadcastManager.register(mReceiver, mActions);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        protected void onLifecycleDestroy() {
            AppBroadcastManager.unregister(mReceiver);
        }
    }
}