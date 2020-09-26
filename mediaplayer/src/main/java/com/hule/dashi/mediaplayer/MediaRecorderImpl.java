package com.hule.dashi.mediaplayer;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;

/**
 * <b>Package:</b> com.hule.dashi.mediaplayer <br>
 * <b>Create Date:</b> 2019/2/19  4:11 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 录音实现 <br>
 */
public class MediaRecorderImpl implements IMediaRecorder {
    private Context mContext;
    private MediaRecorder mMediaRecorder;
    private ArrayList<ObservableEmitter<MediaRecorderInfo>> mRxEmitter = new ArrayList<>();
    /**
     * 当前录制状态
     */
    private MediaRecorderStatusEnum mCurrentStatus;
    /**
     * 输出文件File路径
     */
    private String mOutputFilePath;
    /**
     * 当前配置信息
     */
    private RecorderOption mRecorderOption;
    /**
     * 主线程Handler
     */
    private final Handler mMainHandler;
    /**
     * 语音Id生成器
     */
    private final UUIDGenerator mIdGenerator;
    /**
     * 最小录制时间
     */
    private long mMinDuration;

    public MediaRecorderImpl() {
        mMainHandler = new Handler(Looper.getMainLooper());
        mIdGenerator = new UUIDGenerator();
    }

    @Override
    public void applyMediaOption(RecorderOption recorderOption) {
        this.mRecorderOption = recorderOption;
    }

    @Override
    public Observable<Boolean> startRecord() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                synchronized (MediaRecorderImpl.class) {
                    if (mRecorderOption == null) {
                        emitter.onError(new NullPointerException("调用开始录制前，请调用applyMediaOption()进行相关配置"));
                    }
                    try {
                        MediaRecorder mediaRecorder = getMediaRecorder();
                        notifyPrepared();
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        notifyRecording();
                        emitter.onNext(true);
                        emitter.onComplete();
                    } catch (Throwable e) {
                        releaseRecorder();
                        notifyError(e);
                        emitter.onNext(false);
                    }
                }
            }
        });
    }

    @Override
    public Observable<Boolean> cancelRecord() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                try {
                    finishRecordInternal(true);
                    //不是Debug环境，才删除录音文件
                    if (mRecorderOption != null && !mRecorderOption.isDebug()) {
                        deleteCurrentVoiceFile();
                    }
                    emitter.onNext(true);
                    emitter.onComplete();
                } catch (Throwable e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        });
    }

    private boolean finishRecordInternal(boolean isCancel) {
        releaseRecorder();
        try {
            //获取录音时长
            DefaultMediaPlayer player = new DefaultMediaPlayer();
            player.setDataSource(mOutputFilePath);
            player.prepare();
            int durationMillisecond = player.getDuration();
            ensureMainRun(new Runnable() {
                @Override
                public void run() {
                    //换算为秒
                    double duration = durationMillisecond * 1.0 / 1000;
                    //录制时长小于配置的最小时间，不能录制
                    double minLimitTime = mMinDuration * 1.0 / 1000;
                    if (duration < minLimitTime) {
                        notifyRecordDurationShort();
                    } else {
                        //可能不够1秒，换算可能会为0，所以不够一秒强制为1秒
                        if (duration < 1) {
                            duration = 1;
                        }
                        int finalDuration = (int) Math.round(duration);
                        notifyFinish(mIdGenerator.generate(), mOutputFilePath, finalDuration, isCancel);
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            //ignore
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Observable<Boolean> finishRecord() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                emitter.onNext(finishRecordInternal(false));
            }
        });
    }

    @Override
    public void setContext(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override
    public Observable<MediaRecorderInfo> subscribeMediaRecorder() {
        return Observable.create(new ObservableOnSubscribe<MediaRecorderInfo>() {
            @Override
            public void subscribe(ObservableEmitter<MediaRecorderInfo> emitter) throws Exception {
                mRxEmitter.add(emitter);
            }
        })
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        mRxEmitter.clear();
                    }
                })
                .share();
    }

    @Override
    public boolean deleteTargetVoiceFile(String voiceFilePath) {
        return deleteVoiceFile(voiceFilePath);
    }

    @Override
    public boolean isRecording() {
        return getCurrentStatus() == MediaRecorderStatusEnum.RECORDING;
    }

    private String generateOutputFile(RecorderOption recorderOption) {
        String outputDirPath = recorderOption.getOutputDirPath();
        if (TextUtils.isEmpty(outputDirPath)) {
            try {
                if (recorderOption.isDebug()) {
                    outputDirPath = mContext.getExternalCacheDir().getAbsolutePath();
                } else {
                    //测试将文件放到外部路径，方便调试时拿语音文件
                    outputDirPath = mContext.getExternalFilesDir(null).getAbsolutePath();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                outputDirPath = mContext.getFilesDir().getAbsolutePath();
            }
        }
        String fileName = recorderOption.getFileNameGenerator()
                .generatorName(recorderOption.getFilePrefix(), recorderOption.getFileSuffix());
        File mOutputFile = new File(outputDirPath, fileName);
        return mOutputFile.getAbsolutePath();
    }

    private MediaRecorder getMediaRecorder() {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
            initMediaRecorder();
        }
        return mMediaRecorder;
    }

    /**
     * 初始化MediaRecorder
     */
    private void initMediaRecorder() {
        //注意，这里遇到vivo的5.0机型（厂商私自增加权限管理导致），判断权限会抛异常，又没有异常堆栈！
        try {
            //设置录音的声音来源
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置录音的输出格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            //设置录音的编码格式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            initMediaRecorderOutputFile();
            mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        //到达录制的最大时间了，结束录制
                        finishRecordInternal(false);
                    }
                }
            });
            //最大录制时间
            mMediaRecorder.setMaxDuration((int) mRecorderOption.getMaxDuration());
            mMinDuration = mRecorderOption.getMinDuration();
        } catch (Exception e) {
            e.printStackTrace();
            notifyError(e);
        }
    }


    /**
     * 设置录音文件的输出路径
     */
    private void initMediaRecorderOutputFile() {
        mOutputFilePath = generateOutputFile(mRecorderOption);
        mMediaRecorder.setOutputFile(mOutputFilePath);
    }

    private void releaseRecorder() {
        try {
            if (mMediaRecorder != null) {
                //在stop之前，必须去掉所有监听
                mMediaRecorder.setOnErrorListener(null);
                mMediaRecorder.setOnInfoListener(null);
                mMediaRecorder.setPreviewDisplay(null);
                try {
                    //可能抛出异常，例如在pre准备时就抛出，还没有进行start启动，这时候去stop是不行的
                    mMediaRecorder.stop();
                } catch (IllegalStateException e) {
                    //ignore
                }
                if (mMediaRecorder != null) {
                    mMediaRecorder.reset();
                    mMediaRecorder.release();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            mMediaRecorder = null;
        }
    }

    /**
     * 删除当前的录音文件
     */
    private void deleteCurrentVoiceFile() {
        deleteVoiceFile(mOutputFilePath);
    }

    /**
     * 删除目标录音文件
     *
     * @param targetFilePath 录音文件路劲
     */
    private boolean deleteVoiceFile(String targetFilePath) {
        File file = new File(targetFilePath);
        if (file.exists()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 通知准备中
     */
    private void notifyPrepared() {
        ensureMainRun(new Runnable() {
            @Override
            public void run() {
                mCurrentStatus = MediaRecorderStatusEnum.PREPARED;
                for (ObservableEmitter<MediaRecorderInfo> emitter : mRxEmitter) {
                    emitter.onNext(MediaRecorderInfo.createPrepared());
                }
            }
        });
    }

    /**
     * 通知开始录制
     */
    private void notifyRecording() {
        ensureMainRun(new Runnable() {
            @Override
            public void run() {
                mCurrentStatus = MediaRecorderStatusEnum.RECORDING;
                for (ObservableEmitter<MediaRecorderInfo> emitter : mRxEmitter) {
                    emitter.onNext(MediaRecorderInfo.createRecording());
                }
            }
        });
    }

    /**
     * 通知录制时间太短
     */
    private void notifyRecordDurationShort() {
        ensureMainRun(new Runnable() {
            @Override
            public void run() {
                mCurrentStatus = MediaRecorderStatusEnum.FINISH;
                for (ObservableEmitter<MediaRecorderInfo> emitter : mRxEmitter) {
                    emitter.onNext(MediaRecorderInfo.createRecordDurationShort());
                }
            }
        });
    }

    /**
     * 通知完成
     *
     * @param voiceId       唯一的语音Id
     * @param audioFilePath 录音文件路劲
     * @param audioDuration 录音文件时长
     * @param isCancel      是否取消
     */
    private void notifyFinish(String voiceId, String audioFilePath, int audioDuration, boolean isCancel) {
        ensureMainRun(new Runnable() {
            @Override
            public void run() {
                mCurrentStatus = MediaRecorderStatusEnum.FINISH;
                for (ObservableEmitter<MediaRecorderInfo> emitter : mRxEmitter) {
                    emitter.onNext(MediaRecorderInfo.createFinish(voiceId, audioFilePath, audioDuration, isCancel));
                }
            }
        });
    }

    /**
     * 通知错误
     */
    private void notifyError(Throwable error) {
        ensureMainRun(new Runnable() {
            @Override
            public void run() {
                for (ObservableEmitter<MediaRecorderInfo> emitter : mRxEmitter) {
                    emitter.onNext(MediaRecorderInfo.createError(error));
                }
            }
        });
    }

    /**
     * 确保主线程运行
     */
    private void ensureMainRun(Runnable runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            mMainHandler.post(runnable);
        }
    }

    public MediaRecorderStatusEnum getCurrentStatus() {
        return mCurrentStatus;
    }
}