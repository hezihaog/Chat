package com.zh.android.base.util;

import android.content.Context;
import android.os.Vibrator;

import com.zh.android.contextprovider.ContextProvider;


/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib.utils <br>
 * <b>Create Date:</b> 2019/3/7  12:12 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 震动帮助类 <br>
 */
public class VibratorHelper {
    private VibratorHelper() {
    }

    /**
     * 震动
     */
    public static void startVibrator() {
        Vibrator vibrator = (Vibrator) ContextProvider.get().getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(20);
    }
}