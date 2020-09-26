package com.hule.dashi.mediaplayer;

/**
 * <b>Package:</b> com.hule.dashi.mediaplayer <br>
 * <b>Create Date:</b> 2019/2/19  4:29 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 录音录制信息实体 <br>
 */
public class MediaRecorderInfo {
    private boolean isPrepared = false;
    private boolean isRecording = false;
    private boolean isFinish = false;
    private boolean isError = false;
    private boolean isCancel = false;
    private Throwable error;
    /**
     * 语音Id
     */
    private String voiceId;
    /**
     * 录音文件保存地址
     */
    private String audioFilePath;
    /**
     * 录音时长
     */
    private int audioDuration;
    /**
     * 录制时间是否比配置的录制时间短
     */
    private boolean isRecordDurationShort;

    public static MediaRecorderInfo createPrepared() {
        MediaRecorderInfo info = new MediaRecorderInfo();
        info.isPrepared = true;
        return info;
    }

    public static MediaRecorderInfo createRecording() {
        MediaRecorderInfo info = new MediaRecorderInfo();
        info.isRecording = true;
        return info;
    }

    public static MediaRecorderInfo createFinish(String voiceId, String audioFilePath, int audioDuration, boolean isCancel) {
        MediaRecorderInfo info = new MediaRecorderInfo();
        info.voiceId = voiceId;
        info.isFinish = true;
        info.audioFilePath = audioFilePath;
        info.audioDuration = audioDuration;
        info.isCancel = isCancel;
        return info;
    }

    public static MediaRecorderInfo createError(Throwable error) {
        MediaRecorderInfo info = new MediaRecorderInfo();
        info.isError = true;
        info.error = error;
        return info;
    }

    public static MediaRecorderInfo createRecordDurationShort() {
        MediaRecorderInfo info = new MediaRecorderInfo();
        info.isRecordDurationShort = true;
        return info;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public boolean isError() {
        return isError;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public int getAudioDuration() {
        return audioDuration;
    }

    public Throwable getError() {
        return error;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public boolean isRecordDurationShort() {
        return isRecordDurationShort;
    }
}