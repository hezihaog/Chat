package com.hule.dashi.mediaplayer;

import android.content.Context;

/**
 * <b>Package:</b> com.hule.dashi.mediaplayer <br>
 * <b>Create Date:</b> 2019/3/16  5:55 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class RxMediaRecorder {
    private volatile static RxMediaRecorderManager sManager;

    /**
     * 获取Manger实例
     */
    public static RxMediaRecorderManager getManager(Context context) {
        if (sManager == null) {
            synchronized (RxMediaPlayer.class) {
                if (sManager == null) {
                    sManager = new RxMediaRecorderManager(context.getApplicationContext());
                }
            }
        }
        return sManager;
    }
}