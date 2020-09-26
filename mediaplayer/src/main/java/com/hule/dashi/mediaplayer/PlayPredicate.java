package com.hule.dashi.mediaplayer;

import android.content.Context;
import android.widget.Toast;

import io.reactivex.functions.Predicate;

/**
 * <b>Package:</b> com.hule.dashi.mediaplayer <br>
 * <b>Create Date:</b> 2019/2/15  7:20 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class PlayPredicate implements Predicate<Boolean> {
    private Context mContext;

    public PlayPredicate(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override
    public boolean test(Boolean isPlaySuccess) throws Exception {
        if (!isPlaySuccess) {
            boolean isHandle = onPlayFail();
            if (!isHandle) {
                Toast.makeText(mContext, R.string.mediaplayer_play_voice_fail, Toast.LENGTH_SHORT).show();
            }
        }
        return isPlaySuccess;
    }

    /**
     * 重写该方法，返回true代表已处理，需要自己处理，false则弹出Toast
     */
    public boolean onPlayFail() {
        return false;
    }
}