package com.zh.android.base.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.zh.android.contextprovider.ContextProvider;


/**
 * <b>Package:</b> com.hule.dashi.topic.core <br>
 * <b>FileName:</b> TopicBroadcastManager <br>
 * <b>Create Date:</b> 2018/12/21  下午2:17 <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b> 广播封装 <br>
 */
public class AppBroadcastManager {
    private AppBroadcastManager() {
    }

    public static void register(BroadcastReceiver receiver, String... actions) {
        IntentFilter intentFilter = new IntentFilter();
        for (String action : actions) {
            intentFilter.addAction(action);
        }
        register(receiver, intentFilter);
    }

    public static void register(BroadcastReceiver receiver, IntentFilter filter) {
        try {
            ContextProvider.get().getContext()
                    .registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unregister(BroadcastReceiver receiver) {
        if (receiver == null) {
            return;
        }
        try {
            ContextProvider.get().getContext()
                    .unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-------------- 普通广播 --------------

    public static void sendBroadcast(String action) {
        sendBroadcast(new Intent(action));
    }

    public static void sendBroadcast(String action, Intent intent) {
        intent.setAction(action);
        sendBroadcast(intent);
    }

    public static void sendBroadcast(Intent intent) {
        ContextProvider.get().getContext()
                .sendBroadcast(intent);
    }

    //-------------- 粘性广播 --------------

    public static void sendStickyBroadcast(String action) {
        Intent intent = new Intent(action);
        sendStickyBroadcast(intent);
    }

    public static void sendStickyBroadcast(String action, Intent intent) {
        intent.setAction(action);
        sendStickyBroadcast(intent);
    }

    public static void sendStickyBroadcast(Intent intent) {
        Context context = ContextProvider.get().getContext();
        //发送前先移除
        context.removeStickyBroadcast(intent);
        context.sendStickyBroadcast(intent);
    }

    public static void removeStickyBroadcast(String action) {
        Context context = ContextProvider.get().getContext();
        Intent intent = new Intent(action);
        context.removeStickyBroadcast(intent);
    }
}