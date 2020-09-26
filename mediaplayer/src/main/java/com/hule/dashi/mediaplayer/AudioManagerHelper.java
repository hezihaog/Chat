package com.hule.dashi.mediaplayer;

import android.content.Context;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

/**
 * <b>Package:</b> com.hule.dashi.answer.util.media <br>
 * <b>Create Date:</b> 2019/2/8  6:29 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class AudioManagerHelper {
    private AudioManager mAudioManager;

    public AudioManagerHelper(Context context) {
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 获取焦点
     */
    public boolean requestAudioFocus(AudioManager.OnAudioFocusChangeListener listener) {
        if (mAudioManager != null) {
            int result;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //针对于Android8.0以上
                AudioFocusRequest audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                        .setOnAudioFocusChangeListener(listener).build();
                audioFocusRequest.acceptsDelayedFocusGain();
                result = mAudioManager.requestAudioFocus(audioFocusRequest);
            } else {
                // 小于Android8.0
                result = mAudioManager.requestAudioFocus(listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        return false;
    }

    /**
     * 放弃焦点
     */
    public void abandonAudioFocus(AudioManager.OnAudioFocusChangeListener listener) {
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(listener);
        }
    }
}