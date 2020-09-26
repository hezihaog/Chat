package com.hule.dashi.mediaplayer;

import android.content.Context;

/**
 * <b>Package:</b> com.hule.dashi.mediaplayer <br>
 * <b>Create Date:</b> 2019/2/15  7:13 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 外部门面 <br>
 */
public class RxMediaPlayer {
    private volatile static RxMediaPlayerManager sManager;

    /**
     * 获取Manger实例
     */
    public static RxMediaPlayerManager getManager(Context context) {
        if (sManager == null) {
            synchronized (RxMediaPlayer.class) {
                if (sManager == null) {
                    sManager = new RxMediaPlayerManager(context.getApplicationContext());
                }
            }
        }
        return sManager;
    }
}