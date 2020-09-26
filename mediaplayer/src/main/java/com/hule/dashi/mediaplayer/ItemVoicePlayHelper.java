package com.hule.dashi.mediaplayer;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * <b>Package:</b> com.hule.dashi.answer.consult.util <br>
 * <b>Create Date:</b> 2019/3/16  10:14 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 语音条目点击处理帮助类 <br>
 */
public class ItemVoicePlayHelper {
    private RxMediaPlayerManager mPlayerManager;
    /**
     * Url形式处理器
     */
    private UrlVoiceHandler mUrlVoiceHandler;
    /**
     * FileDescriptor形式处理器
     */
    private FileDescriptorVoiceHandler mFileDescriptorVoiceHandler;

    public ItemVoicePlayHelper(RxMediaPlayerManager playerManager) {
        this.mPlayerManager = playerManager;
    }

    /**
     * 获取Url数据源类型的处理器
     */
    public UrlVoiceHandler getUrlVoiceHandler() {
        if (mUrlVoiceHandler == null) {
            mUrlVoiceHandler = new UrlVoiceHandler(mPlayerManager);
        }
        return mUrlVoiceHandler;
    }

    /**
     * 获取FileDescriptor数据源类型处理器
     */
    public FileDescriptorVoiceHandler getFileDescriptorVoiceHandler() {
        if (mFileDescriptorVoiceHandler == null) {
            mFileDescriptorVoiceHandler = new FileDescriptorVoiceHandler(mPlayerManager);
        }
        return mFileDescriptorVoiceHandler;
    }

    public Observable<Boolean> pause() {
        return mPlayerManager.pause();
    }

    public Observable<Boolean> stop() {
        return mPlayerManager.stop();
    }

    /**
     * 获取File的FileDescriptor
     */
    public FileDescriptor getFileDescriptorFromFile(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        return inputStream.getFD();
    }

    /**
     * 语音处理器接口
     */
    public interface VoiceHandle<T> {
        /**
         * 点击录音条目
         *
         * @param dataSource 数据源
         */
        Observable<Boolean> clickVoice(Context context, T dataSource);

        /**
         * 拉动进度条处理
         *
         * @param dataSource 数据源
         * @param progress   进度
         */
        Observable<Boolean> seekTo(Context context, T dataSource, int progress);

        /**
         * 获取播放中的数据源
         */
        T getPlayingDataSource(MediaOption option);
    }

    /**
     * 处理Url形式的数据源语音
     */
    public static class UrlVoiceHandler implements VoiceHandle<String> {
        private RxMediaPlayerManager mPlayerManager;

        private UrlVoiceHandler(RxMediaPlayerManager playerManager) {
            this.mPlayerManager = playerManager;
        }

        @Override
        public Observable<Boolean> clickVoice(Context context, String dataSource) {
            if (mPlayerManager.isPrepared() || mPlayerManager.isPlaying()) {
                if (mPlayerManager.getApplyMediaOption() != null) {
                    MediaOption mediaOption = mPlayerManager.getApplyMediaOption();
                    MediaDataSource.RealSource<?> realSource = mediaOption.getMediaDataSource().getRealSource();
                    Object source = realSource.getSource();
                    //相同Url则暂停
                    if (source instanceof Uri) {
                        Uri uri = (Uri) source;
                        if (uri.equals(Uri.parse(dataSource))) {
                            return mPlayerManager.pause();
                        } else {
                            //否则打断掉当前播放，播放下一个语音
                            return mPlayerManager.stop()
                                    .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                                        @Override
                                        public ObservableSource<Boolean> apply(Boolean isStopSuccess) throws Exception {
                                            return performPlayVoiceUrl(context, dataSource);
                                        }
                                    });
                        }
                    } else {
                        //不同资源类型互相切换时会走到这里，所以需要先停止掉，再播放新的音频资源
                        return mPlayerManager.stop()
                                .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                                    @Override
                                    public ObservableSource<Boolean> apply(Boolean isStopSuccess) throws Exception {
                                        return performPlayVoiceUrl(context, dataSource);
                                    }
                                });
                    }
                } else {
                    return mPlayerManager.reset();
                }
            } else {
                if (mPlayerManager.isPause()) {
                    MediaOption mediaOption = mPlayerManager.getApplyMediaOption();
                    MediaDataSource.RealSource<?> realSource = mediaOption.getMediaDataSource().getRealSource();
                    Object source = realSource.getSource();
                    //相同Url则继续
                    if (source instanceof Uri) {
                        Uri uri = (Uri) source;
                        if (uri.equals(Uri.parse(dataSource))) {
                            return mPlayerManager.resume();
                        } else {
                            //不同Url，则播放新的
                            return mPlayerManager.stop().flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                                @Override
                                public ObservableSource<Boolean> apply(Boolean isStopSuccess) throws Exception {
                                    return performPlayVoiceUrl(context, dataSource);
                                }
                            });
                        }
                    } else {
                        return Observable.just(false);
                    }
                } else {
                    return performPlayVoiceUrl(context, dataSource);
                }
            }
        }

        @Override
        public Observable<Boolean> seekTo(Context context, String dataSource, int progress) {
            if (mPlayerManager.isPlaying()) {
                if (mPlayerManager.getApplyMediaOption() != null) {
                    MediaOption mediaOption = mPlayerManager.getApplyMediaOption();
                    MediaDataSource.RealSource<?> realSource = mediaOption.getMediaDataSource().getRealSource();
                    Object source = realSource.getSource();
                    //相同Url则暂停
                    if (source instanceof Uri) {
                        Uri uri = (Uri) source;
                        if (uri.equals(Uri.parse(dataSource))) {
                            mPlayerManager.seekTo(progress);
                        } else {
                            //否则打断掉当前播放，播放下一个语音
                            return mPlayerManager.stop().flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                                @Override
                                public ObservableSource<Boolean> apply(Boolean isStopSuccess) throws Exception {
                                    return performPlayVoiceUrl(context, dataSource)
                                            .doOnNext(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(Boolean isOk) throws Exception {
                                                    if (isOk) {
                                                        mPlayerManager.seekTo(progress);
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    }
                }
                return Observable.just(false);
            } else {
                if (mPlayerManager.isPause()) {
                    mPlayerManager.seekTo(progress);
                    return Observable.just(true);
                } else {
                    return performPlayVoiceUrl(context, dataSource)
                            .doOnNext(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean isOk) throws Exception {
                                    if (isOk) {
                                        mPlayerManager.seekTo(progress);
                                    }
                                }
                            });
                }
            }
        }

        @Override
        public String getPlayingDataSource(MediaOption option) {
            MediaDataSource.RealSource<?> realSource = option.getMediaDataSource().getRealSource();
            if (realSource.getSource() instanceof Uri) {
                Uri source = (Uri) realSource.getSource();
                return source.toString();
            }
            return null;
        }

        /**
         * 播放指定Url数据源
         */
        private Observable<Boolean> performPlayVoiceUrl(Context context, String voiceUrl) {
            mPlayerManager.applyMediaOption(
                    new MediaOption.Builder(new MediaDataSource(Uri.parse(voiceUrl))).build());
            return Observable.just(mPlayerManager)
                    .flatMap(new Function<RxMediaPlayerManager, ObservableSource<Boolean>>() {
                        @Override
                        public ObservableSource<Boolean> apply(RxMediaPlayerManager mediaPlayerManager) throws Exception {
                            return mediaPlayerManager.start();
                        }
                    })
                    .filter(new PlayPredicate(context));
        }

        /**
         * 判断Url是否是当前播放的
         */
        protected boolean isPlayingUrl(String itemMediaUrl) {
            if (mPlayerManager != null && mPlayerManager.getApplyMediaOption() != null
                    && mPlayerManager.getApplyMediaOption().getMediaDataSource() != null
                    && mPlayerManager.getApplyMediaOption().getMediaDataSource().getRealSource() != null) {
                MediaOption option = mPlayerManager.getApplyMediaOption();
                MediaDataSource.RealSource<?> realSource = option.getMediaDataSource().getRealSource();
                if (realSource.getSource() instanceof Uri) {
                    Uri source = (Uri) realSource.getSource();
                    return isItemPlayMedia(itemMediaUrl, source);
                }
            }
            return false;
        }

        /**
         * 判断当前播放的音频Url是否是Item模型中的Url
         */
        protected boolean isItemPlayMedia(String itemMediaUrl, Uri currentPlayerUrl) {
            if (TextUtils.isEmpty(itemMediaUrl) || currentPlayerUrl == null) {
                return false;
            }
            return Uri.parse(itemMediaUrl).equals(currentPlayerUrl);
        }
    }

    /**
     * 包裹UUDI和FileDescriptor
     */
    public static class FileDescriptorWithVoiceId {
        private String voiceId;
        private FileDescriptor fileDescriptor;

        public FileDescriptorWithVoiceId(String voiceId, FileDescriptor fileDescriptor) {
            this.voiceId = voiceId;
            this.fileDescriptor = fileDescriptor;
        }

        public String getVoiceId() {
            return voiceId;
        }

        public FileDescriptor getFileDescriptor() {
            return fileDescriptor;
        }
    }

    /**
     * FileDescriptor形式资源的语音处理器
     */
    public static class FileDescriptorVoiceHandler implements VoiceHandle<FileDescriptorWithVoiceId> {
        private RxMediaPlayerManager mPlayerManager;

        private FileDescriptorVoiceHandler(RxMediaPlayerManager playerManager) {
            mPlayerManager = playerManager;
        }

        @Override
        public Observable<Boolean> clickVoice(Context context, FileDescriptorWithVoiceId targetDataSource) {
            if (mPlayerManager.isPrepared() || mPlayerManager.isPlaying()) {
                if (mPlayerManager.getApplyMediaOption() != null) {
                    MediaOption mediaOption = mPlayerManager.getApplyMediaOption();
                    MediaDataSource.RealSource<?> realSource = mediaOption.getMediaDataSource().getRealSource();
                    String voiceId = mediaOption.getMediaDataSource().getVoiceId();
                    Object source = realSource.getSource();
                    //相同资源则暂停
                    if (source instanceof FileDescriptor) {
                        if (voiceId.equals(targetDataSource.getVoiceId())) {
                            return mPlayerManager.pause();
                        } else {
                            //否则打断掉当前播放，播放下一个语音
                            return mPlayerManager.stop()
                                    .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                                        @Override
                                        public ObservableSource<Boolean> apply(Boolean isStopSuccess) throws Exception {
                                            return performPlayVoiceByFileDescriptor(
                                                    context,
                                                    targetDataSource.getVoiceId(),
                                                    targetDataSource.getFileDescriptor());
                                        }
                                    });
                        }
                    } else {
                        //不同资源类型互相切换时会走到这里，所以需要先停止掉，再播放新的音频资源
                        return mPlayerManager.stop()
                                .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                                    @Override
                                    public ObservableSource<Boolean> apply(Boolean isStopSuccess) throws Exception {
                                        return performPlayVoiceByFileDescriptor(
                                                context,
                                                targetDataSource.getVoiceId(),
                                                targetDataSource.getFileDescriptor());
                                    }
                                });
                    }
                } else {
                    return mPlayerManager.reset();
                }
            } else {
                if (mPlayerManager.isPause()) {
                    return mPlayerManager.resume();
                } else {
                    return performPlayVoiceByFileDescriptor(
                            context,
                            targetDataSource.getVoiceId(),
                            targetDataSource.getFileDescriptor());
                }
            }
        }

        @Override
        public Observable<Boolean> seekTo(Context context, FileDescriptorWithVoiceId targetDataSource, int progress) {
            if (mPlayerManager.isPlaying()) {
                if (mPlayerManager.getApplyMediaOption() != null) {
                    MediaOption mediaOption = mPlayerManager.getApplyMediaOption();
                    String voiceId = mediaOption.getMediaDataSource().getVoiceId();
                    MediaDataSource.RealSource<?> realSource = mediaOption.getMediaDataSource().getRealSource();
                    Object source = realSource.getSource();
                    //相同Url则暂停
                    if (source instanceof FileDescriptor) {
                        if (voiceId.equals(targetDataSource.getVoiceId())) {
                            mPlayerManager.seekTo(progress);
                        } else {
                            //否则打断掉当前播放，播放下一个语音
                            return mPlayerManager.stop().flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                                @Override
                                public ObservableSource<Boolean> apply(Boolean isStopSuccess) throws Exception {
                                    return performPlayVoiceByFileDescriptor(
                                            context,
                                            targetDataSource.getVoiceId(),
                                            targetDataSource.getFileDescriptor())
                                            .doOnNext(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(Boolean aBoolean) throws Exception {
                                                    mPlayerManager.seekTo(progress);
                                                }
                                            });
                                }
                            });
                        }
                    }
                }
                return Observable.just(false);
            } else {
                if (mPlayerManager.isPause()) {
                    mPlayerManager.seekTo(progress);
                    return Observable.just(true);
                } else {
                    return performPlayVoiceByFileDescriptor(
                            context,
                            targetDataSource.getVoiceId(),
                            targetDataSource.getFileDescriptor())
                            .doOnNext(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean aBoolean) throws Exception {
                                    mPlayerManager.seekTo(progress);
                                }
                            });
                }
            }
        }

        @Override
        public FileDescriptorWithVoiceId getPlayingDataSource(MediaOption option) {
            MediaDataSource.RealSource<?> realSource = option.getMediaDataSource().getRealSource();
            if (realSource.getSource() instanceof FileDescriptor) {
                String voiceId = option.getMediaDataSource().getVoiceId();
                FileDescriptor source = (FileDescriptor) realSource.getSource();
                return new FileDescriptorWithVoiceId(voiceId, source);
            }
            return null;
        }

        /**
         * 播放指定FileDescriptor数据源
         */
        private Observable<Boolean> performPlayVoiceByFileDescriptor(Context context, String voiceId, FileDescriptor fileDescriptor) {
            mPlayerManager.applyMediaOption(
                    new MediaOption.Builder(new MediaDataSource(voiceId, fileDescriptor)).build());
            return Observable.just(mPlayerManager)
                    .flatMap(new Function<RxMediaPlayerManager, ObservableSource<Boolean>>() {
                        @Override
                        public ObservableSource<Boolean> apply(RxMediaPlayerManager mediaPlayerManager) throws Exception {
                            return mediaPlayerManager.start();
                        }
                    })
                    .filter(new PlayPredicate(context));
        }
    }
}