package com.hule.dashi.mediaplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;

/**
 * <b>Package:</b> com.hule.dashi.answer.util.media <br>
 * <b>Create Date:</b> 2019/2/5  6:28 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 多媒体播放器实现 <br>
 */
public class MediaPlayerImpl implements IMediaPlayer, AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = MediaPlayerImpl.class.getName();

    /**
     * 检查进度更新间隔时间
     */
    private static final int CHECK_PROGRESS_INTERVAL_TIME = 300;

    private DefaultMediaPlayer mMediaPlayer;
    private Context mContext;
    private MediaOption mMediaOption;
    private MediaPlayerListener.OnPlayStatusUpdateListener mOnPlayStatusUpdateListener;
    /**
     * 当前播放状态
     */
    private MediaPlayStatusEnum mCurrentPlayStatus;
    private WifiManager.WifiLock mWifiLock;
    private AudioManagerHelper mAudioManagerHelper;
    /**
     * 注册的发射器集合
     */
    private ArrayList<ObservableEmitter<MediaPlayInfo>> mRxEmitterList = new ArrayList<>();
    /**
     * 播放进度定时Handler
     */
    private Handler mProgressCheckHandler;
    /**
     * 播放进度定时任务
     */
    private Runnable mProgressCheckRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying()) {
                if (mOnProgressUpdateListener != null) {
                    mOnProgressUpdateListener.onProgressUpdate(getApplyMediaOption(), getProgress());
                }
            }
            mProgressCheckHandler.postDelayed(mProgressCheckRunnable, CHECK_PROGRESS_INTERVAL_TIME);
        }
    };
    /**
     * 外部注册的进度更新回调
     */
    private MediaPlayerListener.OnProgressUpdateListener mOnProgressUpdateListener;

    public MediaPlayerImpl() {
        mProgressCheckHandler = new Handler(Looper.getMainLooper());
        mProgressCheckHandler.postDelayed(mProgressCheckRunnable, CHECK_PROGRESS_INTERVAL_TIME);
    }

    @Override
    public Observable<Boolean> start() {
        if (mMediaOption == null) {
            throw new NullPointerException("确保调用start方法前，先调用applyMediaOption方法初始化必要的参数");
        }
        DefaultMediaPlayer player = getMediaPlayer();
        setVolume(mMediaOption.getLeftVolume(), mMediaOption.getRightVolume());
        player.setLooping(mMediaOption.isLoop());
        return playSound(true);
    }

    /**
     * 开始播放
     *
     * @param isNeedReset 是否需要重置
     */
    private Observable<Boolean> playSound(boolean isNeedReset) {
        return Observable.just(getMediaPlayer())
                .map(new Function<DefaultMediaPlayer, Boolean>() {
                    @Override
                    public Boolean apply(DefaultMediaPlayer player) throws Exception {
                        //获取焦点
                        boolean isGainFocus = mAudioManagerHelper.requestAudioFocus(MediaPlayerImpl.this);
                        if (isGainFocus) {
                            //打断正在播放的音乐
                            if (isNeedReset && player.isPlaying()) {
                                player.reset();
                            }
                            notifyPrepared();
                            if (isNeedReset) {
                                player.prepare();
                            }
                            player.start();
                            //启用Wifi锁
                            mWifiLock.acquire();
                            if (mOnPlayStatusUpdateListener != null) {
                                MediaPlayStatusEnum preStatus = mCurrentPlayStatus;
                                mCurrentPlayStatus = MediaPlayStatusEnum.PLAYING;
                                mOnPlayStatusUpdateListener.onPlayInfoUpdate(preStatus, mCurrentPlayStatus);
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
    }

    @Override
    public Observable<Boolean> stop() {
        return Observable.just(stopNow());
    }

    private boolean stopNow() {
        DefaultMediaPlayer player = getMediaPlayer();
        if (player.isPlaying()) {
            mAudioManagerHelper.abandonAudioFocus(this);
            player.stop();
            if (mOnPlayStatusUpdateListener != null) {
                MediaPlayStatusEnum preStatus = mCurrentPlayStatus;
                mCurrentPlayStatus = MediaPlayStatusEnum.STOPPED;
                mOnPlayStatusUpdateListener.onPlayInfoUpdate(preStatus, mCurrentPlayStatus);
            }
        }
        return true;
    }

    @Override
    public Observable<Boolean> pause() {
        return Observable.just(getMediaPlayer())
                .map(new Function<DefaultMediaPlayer, Boolean>() {
                    @Override
                    public Boolean apply(DefaultMediaPlayer player) throws Exception {
                        if (player.isPlaying()) {
                            player.pause();
                            mAudioManagerHelper.abandonAudioFocus(MediaPlayerImpl.this);
                            //释放Wifi锁
                            if (mWifiLock.isHeld()) {
                                mWifiLock.release();
                            }
                            if (mOnPlayStatusUpdateListener != null) {
                                MediaPlayStatusEnum preStatus = mCurrentPlayStatus;
                                mCurrentPlayStatus = MediaPlayStatusEnum.PAUSE;
                                mOnPlayStatusUpdateListener.onPlayInfoUpdate(preStatus, mCurrentPlayStatus);
                            }
                        }
                        return true;
                    }
                });
    }

    @Override
    public Observable<Boolean> resume() {
        return Observable.just(getMediaPlayer())
                .flatMap(new Function<DefaultMediaPlayer, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(DefaultMediaPlayer player) throws Exception {
                        if (player.isPause()) {
                            return playSound(false);
                        } else {
                            return Observable.just(true);
                        }
                    }
                });
    }

    @Override
    public Observable<Boolean> reset() {
        return Observable.just(getMediaPlayer())
                .map(new Function<DefaultMediaPlayer, Boolean>() {
                    @Override
                    public Boolean apply(DefaultMediaPlayer defaultMediaPlayer) throws Exception {
                        MediaPlayer player = getMediaPlayer();
                        if (player.isPlaying()) {
                            player.reset();
                        }
                        return true;
                    }
                });
    }

    private void resetNow() {
        MediaPlayer player = getMediaPlayer();
        if (player.isPlaying()) {
            player.reset();
        }
    }

    @Override
    public Observable<Boolean> release() {
        return Observable.just(releaseNow());
    }

    @Override
    public void seekTo(int progress) {
        getMediaPlayer().seekTo((int) (getMediaPlayer().getDuration() * progress / 100.0f));
    }

    @Override
    public void setOnProgressUpdateListener(MediaPlayerListener.OnProgressUpdateListener listener) {
        mOnProgressUpdateListener = listener;
    }

    private boolean releaseNow() {
        DefaultMediaPlayer player = getUnsafeMediaPlayer();
        if (player != null) {
            player.release();
            mMediaPlayer = null;
            //释放Wifi锁
            if (mWifiLock.isHeld()) {
                mWifiLock.release();
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     * 销毁播放器
     */
    private void destroyPlayer() {
        stopNow();
        releaseNow();
    }

    @Override
    public void applyMediaOption(MediaOption mediaOption) {
        //避免同一个Url连续点击2次，导致状态不正确而崩溃
        MediaOption currentApplyOption = mMediaOption;
        if (currentApplyOption != null) {
            if (mCurrentPlayStatus == MediaPlayStatusEnum.PREPARED) {
                Object playingSource = currentApplyOption.getMediaDataSource().getRealSource().getSource();
                Object targetSource = mediaOption.getMediaDataSource().getRealSource().getSource();
                if (playingSource instanceof Uri && targetSource instanceof Uri) {
                    String playingUrl = playingSource.toString();
                    String targetUrl = targetSource.toString();
                    if (playingUrl.equals(targetUrl)) {
                        return;
                    }
                }
            }
        }
        //开始配置
        this.mMediaOption = mediaOption;
        MediaDataSource mediaDataSource = mediaOption.getMediaDataSource();
        //设置数据源
        if (mediaDataSource.getRealSource() == null) {
            throw new NullPointerException("MediaDataSource source must be not null");
        }
        MediaDataSource.RealSource<?> realSource = mediaDataSource.getRealSource();
        try {
            MediaPlayer player = getMediaPlayer();
            player.reset();
            MediaDataSourceHelper helper = new MediaDataSourceHelper(mContext);
            helper.dataSourceCompat(player, realSource,
                    mediaOption.getOffset(), mediaOption.getLength());
            //设置音量
            setVolume(mediaOption.getLeftVolume(), mediaOption.getRightVolume());
            //设置是否循环播放
            player.setLooping(mediaOption.isLoop());
        } catch (Exception e) {
            e.printStackTrace();
            destroyPlayer();
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        DefaultMediaPlayer player = getMediaPlayer();
        if (leftVolume > 1) {
            leftVolume = 1;
        } else if (leftVolume < 0) {
            leftVolume = 0;
        }
        if (rightVolume > 1) {
            rightVolume = 1;
        } else if (rightVolume < 0) {
            rightVolume = 0;
        }
        player.setVolume(leftVolume, rightVolume);
    }

    @Override
    public void setContext(Context context) {
        mContext = context.getApplicationContext();
        if (mWifiLock == null) {
            WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            mWifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
        }
        mAudioManagerHelper = new AudioManagerHelper(context);
    }

    public void setOnErrorListener(MediaPlayerListener.OnErrorListener onErrorListener) {
        MediaPlayerImpl mediaPlayerImpl = this;
        getMediaPlayer().setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mediaPlayerImpl.reset();
                if (onErrorListener != null) {
                    return onErrorListener.onError(mp, what, extra);
                }
                return false;
            }
        });
    }

    @Override
    public void setOnBufferingUpdateListener(MediaPlayerListener.OnBufferingUpdateListener onBufferingUpdateListener) {
        getMediaPlayer().setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (onBufferingUpdateListener != null) {
                    onBufferingUpdateListener.onBufferingUpdate(mp, percent);
                }
            }
        });
    }

    @Override
    public void setOnSeekCompleteListener(MediaPlayerListener.OnSeekCompleteListener onSeekCompleteListener) {
        getMediaPlayer().setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if (onSeekCompleteListener != null) {
                    onSeekCompleteListener.onSeekComplete(mp);
                }
            }
        });
    }

    private void setOnPlayStatusUpdateListener(MediaPlayerListener.OnPlayStatusUpdateListener onPlayStatusUpdateListener) {
        this.mOnPlayStatusUpdateListener = onPlayStatusUpdateListener;
    }

    /**
     * 通知准备播放
     */
    private void notifyPrepared() {
        if (mOnPlayStatusUpdateListener != null) {
            MediaPlayStatusEnum preStatus = mCurrentPlayStatus;
            mCurrentPlayStatus = MediaPlayStatusEnum.PREPARED;
            mOnPlayStatusUpdateListener.onPlayInfoUpdate(preStatus, mCurrentPlayStatus);
        }
    }

    /**
     * 注册监听
     */
    private void initMediaPlayerListener() {
        //播放完成的回调
        getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mOnPlayStatusUpdateListener != null) {
                    MediaPlayStatusEnum preStatus = mCurrentPlayStatus;
                    mCurrentPlayStatus = MediaPlayStatusEnum.COMPLETION;
                    mOnPlayStatusUpdateListener.onPlayInfoUpdate(preStatus, mCurrentPlayStatus);
                }
            }
        });
        //播放错误的回调
        setOnErrorListener(new MediaPlayerListener.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer player, int what, int extra) {
                for (ObservableEmitter<MediaPlayInfo> emitter : mRxEmitterList) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(MediaPlayInfo.createError(getApplyMediaOption()));
                    }
                }
                return true;
            }
        });
        //播放进度更新监听
        setOnPlayStatusUpdateListener(new MediaPlayerListener.OnPlayStatusUpdateListener() {

            @Override
            public void onPlayInfoUpdate(MediaPlayStatusEnum preStatus, MediaPlayStatusEnum newStatus) {
                for (ObservableEmitter<MediaPlayInfo> emitter : mRxEmitterList) {
                    if (!emitter.isDisposed()) {
                        if (MediaPlayStatusEnum.PREPARED == newStatus) {
                            emitter.onNext(MediaPlayInfo.createPrepared(getApplyMediaOption()));
                        } else if (MediaPlayStatusEnum.PLAYING == newStatus) {
                            emitter.onNext(MediaPlayInfo.createPlaying(getApplyMediaOption()));
                        } else if (MediaPlayStatusEnum.PAUSE == newStatus) {
                            emitter.onNext(MediaPlayInfo.createPause(getApplyMediaOption()));
                        } else if (MediaPlayStatusEnum.STOPPED == newStatus) {
                            emitter.onNext(MediaPlayInfo.createStopped(getApplyMediaOption()));
                            destroyPlayer();
                        } else if (MediaPlayStatusEnum.COMPLETION == newStatus) {
                            emitter.onNext(MediaPlayInfo.createCompletion(getApplyMediaOption()));
                            destroyPlayer();
                        }
                    }
                }
            }
        });
    }

    private int getProgress() {
        float progress = getMediaPlayer().getCurrentPosition() * 1.0f / getMediaPlayer().getDuration();
        return (int) (progress * 100);
    }

    /**
     * 观察播放情况
     */
    @Override
    public Observable<MediaPlayInfo> subscribeMediaPlayer() {
        return Observable.create(new ObservableOnSubscribe<MediaPlayInfo>() {
            @Override
            public void subscribe(ObservableEmitter<MediaPlayInfo> emitter) throws Exception {
                mRxEmitterList.add(emitter);
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                //所有观察者都取消注册，回收播放器
                destroyPlayer();
                mRxEmitterList.clear();
            }
        }).share();
    }

    /**
     * 获取MediaPlayer，保证不为null
     */
    private DefaultMediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new DefaultMediaPlayer();
            //对每个Player对象都注册监听
            initMediaPlayerListener();
            mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
        }
        return mMediaPlayer;
    }

    /**
     * 忽略空的情况
     */
    private DefaultMediaPlayer getUnsafeMediaPlayer() {
        return mMediaPlayer;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        DefaultMediaPlayer player = getMediaPlayer();
        //播放器焦点切换
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                //重新获得焦点
                if (player.isPause()) {
                    resume();
                }
                setVolume(mMediaOption.getLeftVolume(), mMediaOption.getRightVolume());
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                //永久失去焦点，例如被其他播放器抢占
                if (player.isPlaying() || player.isLoading()) {
                    mAudioManagerHelper.abandonAudioFocus(this);
                    pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                //短暂失去焦点，例如来电
                if (player.isPlaying() || player.isLoading()) {
                    pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                //瞬间失去焦点，例如通知
                if (player.isPlaying() || player.isLoading()) {
                    if (mMediaPlayer.isPlaying()) {
                        setVolume(0.1f, 0.1f);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isPrepared() {
        return MediaPlayStatusEnum.PREPARED == mCurrentPlayStatus;
    }

    @Override
    public boolean isPlaying() {
        return getMediaPlayer().isPlaying();
    }

    @Override
    public boolean isPause() {
        return getMediaPlayer().isPause();
    }

    @Override
    public MediaOption getApplyMediaOption() {
        return this.mMediaOption;
    }
}