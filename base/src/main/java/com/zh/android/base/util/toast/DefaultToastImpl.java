package com.zh.android.base.util.toast;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib.utils.toast <br>
 * <b>Create Date:</b> 2019-06-12  16:18 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 系统默认Toast实现 <br>
 */
public class DefaultToastImpl implements IToast {

    @Override
    public void showMsg(Context context, int str) {
        if (context == null) {
            return;
        }
        showMsg(context, context.getResources().getString(str));
    }

    @Override
    public void showMsg(Context context, String str) {
        showToast(context, str, Toast.LENGTH_SHORT);
    }

    @Override
    public void showMsgLong(Context context, int str) {
        if (context == null) {
            return;
        }
        showMsgLong(context, context.getResources().getString(str));
    }

    @Override
    public void showMsgLong(Context context, String str) {
        showToast(context, str, Toast.LENGTH_LONG);
    }

    private void showToast(Context context, String str, int duration) {
        if (context == null) {
            return;
        }
        Context applicationContext = context.getApplicationContext();
        Runnable runnable = () -> {
            Toast toast = Toast.makeText(applicationContext, str, duration);
            toast.show();
        };
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            new Handler(Looper.getMainLooper()).post(runnable);
        }
    }
}