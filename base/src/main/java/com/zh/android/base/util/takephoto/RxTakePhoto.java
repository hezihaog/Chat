package com.zh.android.base.util.takephoto;


import androidx.fragment.app.FragmentActivity;

import com.zh.android.base.lifecycle.DelegateFragmentFinder;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib <br>
 * <b>Create Date:</b> 2019-04-30  12:32 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> RxJava封装选择图片 <br>
 */
public class RxTakePhoto {
    /**
     * 从相机中拍照选择图片，不裁剪
     */
    public Observable<TakePhotoEvent> takeImageByCamera(FragmentActivity activity) {
        return takeImageByCamera(activity, false);
    }

    /**
     * 从相机中拍照选择图片，可以设置是否剪裁
     *
     * @param isCrop 是否需要裁剪
     */
    public Observable<TakePhotoEvent> takeImageByCamera(FragmentActivity activity, boolean isCrop) {
        return getTakePhotoObservable(activity)
                .flatMap(new Function<TakePhotoDelegateFragment, ObservableSource<TakePhotoEvent>>() {
                    @Override
                    public ObservableSource<TakePhotoEvent> apply(TakePhotoDelegateFragment fragment) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<TakePhotoEvent>() {
                            @Override
                            public void subscribe(ObservableEmitter<TakePhotoEvent> emitter) throws Exception {
                                fragment.takeImageByCamera(new TakePhotoDelegateFragment.OnTakePhotoCallback() {
                                    @Override
                                    public void onTakePhoto(ArrayList<String> imgPaths) {
                                        emitter.onNext(createTakePhotoSuccessEvent(imgPaths));
                                    }

                                    @Override
                                    public void onTakeCancel() {
                                        emitter.onNext(createTakePhotoCancelEvent());
                                    }
                                }, isCrop);
                            }
                        });
                    }
                });
    }

    /**
     * 从图库中选择图片，只选择1张图片
     */
    public Observable<TakePhotoEvent> takeImageByGallery(FragmentActivity activity, boolean isCrop) {
        return takeImageByGallery(activity, 1, isCrop);
    }

    /**
     * 从图库中选择图片，可以设定还剩余多少张
     */
    public Observable<TakePhotoEvent> takeImageByGallery(FragmentActivity activity, int residueSelectPicCount, boolean isCrop) {
        return getTakePhotoObservable(activity)
                .flatMap(new Function<TakePhotoDelegateFragment, ObservableSource<TakePhotoEvent>>() {
                    @Override
                    public ObservableSource<TakePhotoEvent> apply(TakePhotoDelegateFragment fragment) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<TakePhotoEvent>() {
                            @Override
                            public void subscribe(ObservableEmitter<TakePhotoEvent> emitter) throws Exception {
                                fragment.takeImageByGallery(new TakePhotoDelegateFragment.OnTakePhotoCallback() {
                                    @Override
                                    public void onTakePhoto(ArrayList<String> imgPaths) {
                                        emitter.onNext(createTakePhotoSuccessEvent(imgPaths));
                                    }

                                    @Override
                                    public void onTakeCancel() {
                                        emitter.onNext(createTakePhotoCancelEvent());
                                    }
                                }, residueSelectPicCount, isCrop);
                            }
                        });
                    }
                });
    }

    /**
     * 从相机中拍照选择视频
     */
    public Observable<TakePhotoEvent> takeVideoByCamera(FragmentActivity activity) {
        return getTakePhotoObservable(activity)
                .flatMap(new Function<TakePhotoDelegateFragment, ObservableSource<TakePhotoEvent>>() {
                    @Override
                    public ObservableSource<TakePhotoEvent> apply(TakePhotoDelegateFragment fragment) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<TakePhotoEvent>() {
                            @Override
                            public void subscribe(ObservableEmitter<TakePhotoEvent> emitter) throws Exception {
                                fragment.takeVideoByCamera(new TakePhotoDelegateFragment.OnTakePhotoCallback() {
                                    @Override
                                    public void onTakePhoto(ArrayList<String> imgPaths) {
                                        emitter.onNext(createTakePhotoSuccessEvent(imgPaths));
                                    }

                                    @Override
                                    public void onTakeCancel() {
                                        emitter.onNext(createTakePhotoCancelEvent());
                                    }
                                });
                            }
                        });
                    }
                });
    }

    /**
     * 从图库中选择视频，可以设定还剩余多少张
     */
    public Observable<TakePhotoEvent> takeVideoByGallery(FragmentActivity activity, int residueSelectPicCount) {
        return getTakePhotoObservable(activity)
                .flatMap(new Function<TakePhotoDelegateFragment, ObservableSource<TakePhotoEvent>>() {
                    @Override
                    public ObservableSource<TakePhotoEvent> apply(TakePhotoDelegateFragment fragment) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<TakePhotoEvent>() {
                            @Override
                            public void subscribe(ObservableEmitter<TakePhotoEvent> emitter) throws Exception {
                                fragment.takeVideoByGallery(new TakePhotoDelegateFragment.OnTakePhotoCallback() {
                                    @Override
                                    public void onTakePhoto(ArrayList<String> imgPaths) {
                                        emitter.onNext(createTakePhotoSuccessEvent(imgPaths));
                                    }

                                    @Override
                                    public void onTakeCancel() {
                                        emitter.onNext(createTakePhotoCancelEvent());
                                    }
                                }, residueSelectPicCount);
                            }
                        });
                    }
                });
    }

    public static class TakePhotoEvent {
        /**
         * 是否用户取消选择
         */
        private boolean isTakeCancel;
        /**
         * 选择的图片路径
         */
        private ArrayList<String> imgPaths;

        public boolean isTakeCancel() {
            return isTakeCancel;
        }

        public void setTakeCancel(boolean takeCancel) {
            isTakeCancel = takeCancel;
        }

        public ArrayList<String> getImgPaths() {
            return imgPaths;
        }

        public void setImgPaths(ArrayList<String> imgPaths) {
            this.imgPaths = imgPaths;
        }
    }

    private TakePhotoEvent createTakePhotoSuccessEvent(ArrayList<String> imgPaths) {
        TakePhotoEvent event = new TakePhotoEvent();
        event.setImgPaths(imgPaths);
        event.setTakeCancel(false);
        return event;
    }

    private TakePhotoEvent createTakePhotoCancelEvent() {
        TakePhotoEvent event = new TakePhotoEvent();
        event.setTakeCancel(true);
        return event;
    }

    /**
     * 获取TakePhotoDelegateFragment并包裹为Observable
     */
    private Observable<TakePhotoDelegateFragment> getTakePhotoObservable(FragmentActivity activity) {
        return Observable.create(new ObservableOnSubscribe<FragmentActivity>() {
            @Override
            public void subscribe(ObservableEmitter<FragmentActivity> emitter) throws Exception {
                if (activity == null || activity.isFinishing()) {
                    emitter.onError(new NullPointerException());
                } else {
                    emitter.onNext(activity);
                }
            }
        }).map(new Function<FragmentActivity, TakePhotoDelegateFragment>() {
            @Override
            public TakePhotoDelegateFragment apply(FragmentActivity activity) throws Exception {
                return DelegateFragmentFinder
                        .getInstance().find(activity, TakePhotoDelegateFragment.class);
            }
        });
    }
}