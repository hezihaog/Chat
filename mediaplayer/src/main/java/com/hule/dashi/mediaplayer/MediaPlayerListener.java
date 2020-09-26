package com.hule.dashi.mediaplayer;

import android.media.MediaPlayer;

/**
 * <b>Package:</b> com.hule.dashi.answer.util.media <br>
 * <b>Create Date:</b> 2019/2/5  7:49 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 媒体播放器监听器接口聚合 <br>
 */
public interface MediaPlayerListener {
    /**
     * 媒体播放错误监听
     */
    interface OnErrorListener {
        boolean onError(MediaPlayer player, int what, int extra);
    }

    /**
     * 网络流媒体缓存监听
     */
    interface OnBufferingUpdateListener {
        void onBufferingUpdate(MediaPlayer player, int percent);
    }

    /**
     * 进度调整完成监听，主要是配合seekTo使用
     */
    interface OnSeekCompleteListener {
        void onSeekComplete(MediaPlayer player);
    }

    interface OnProgressUpdateListener {
        /**
         * 进度更新
         *
         * @param option   当前使用的播放配置
         * @param progress 当前进度
         */
        void onProgressUpdate(MediaOption option, int progress);
    }

    /**
     * 状态更新监听
     */
    interface OnPlayStatusUpdateListener {
        void onPlayInfoUpdate(MediaPlayStatusEnum preStatus, MediaPlayStatusEnum newStatus);
    }
}