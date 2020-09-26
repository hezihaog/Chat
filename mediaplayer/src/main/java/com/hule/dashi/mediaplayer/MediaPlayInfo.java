package com.hule.dashi.mediaplayer;

/**
 * <b>Package:</b> com.hule.dashi.answer.util.media <br>
 * <b>Create Date:</b> 2019/2/5  9:34 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 播放信息实体 <br>
 */
public class MediaPlayInfo {
    private boolean isPrepared = false;
    private boolean isPlaying = false;
    private boolean isPause = false;
    private boolean isStopped = false;
    private boolean isCompletion = false;
    private boolean isError = false;
    private boolean isUpdateProgress = false;
    //当前进度
    private int progress = 0;
    /**
     * 当前应用的配置
     */
    private MediaOption applyMediaOption;

    private MediaPlayInfo() {
    }

    public static MediaPlayInfo createPrepared(MediaOption applyMediaOption) {
        MediaPlayInfo info = new MediaPlayInfo();
        info.isPrepared = true;
        info.applyMediaOption = applyMediaOption;
        return info;
    }

    public static MediaPlayInfo createPlaying(MediaOption applyMediaOption) {
        MediaPlayInfo info = new MediaPlayInfo();
        info.isPlaying = true;
        info.applyMediaOption = applyMediaOption;
        return info;
    }

    public static MediaPlayInfo createPause(MediaOption applyMediaOption) {
        MediaPlayInfo info = new MediaPlayInfo();
        info.isPause = true;
        info.applyMediaOption = applyMediaOption;
        return info;
    }

    public static MediaPlayInfo createStopped(MediaOption applyMediaOption) {
        MediaPlayInfo info = new MediaPlayInfo();
        info.isStopped = true;
        info.applyMediaOption = applyMediaOption;
        return info;
    }

    public static MediaPlayInfo createCompletion(MediaOption applyMediaOption) {
        MediaPlayInfo info = new MediaPlayInfo();
        info.isCompletion = true;
        info.applyMediaOption = applyMediaOption;
        return info;
    }

    public static MediaPlayInfo createError(MediaOption applyMediaOption) {
        MediaPlayInfo info = new MediaPlayInfo();
        info.isError = true;
        info.applyMediaOption = applyMediaOption;
        return info;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPause() {
        return isPause;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public boolean isCompletion() {
        return isCompletion;
    } public boolean isUpdateProgress() {
        return isUpdateProgress;
    }

    public boolean isError() {
        return isError;
    }

    public MediaOption getApplyMediaOption() {
        return applyMediaOption;
    }

    public int getProgress() {
        return progress;
    }
}