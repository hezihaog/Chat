package com.zh.android.base.util.takephoto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;


import androidx.annotation.Nullable;

import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zh.android.base.R;
import com.zh.android.base.lifecycle.AppDelegateFragment;
import com.zh.android.base.lifecycle.BaseDelegateFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * <b>Package:</b> com.hule.dashi.topic <br>
 * <b>FileName:</b> TakePhotoDelegateFragment <br>
 * <b>Create Date:</b> 2018/12/27  3:35 PM <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class TakePhotoDelegateFragment extends AppDelegateFragment {
    public static final String RECOMMEND_REQUEST_EXTRA = "img_list";
    public static final int RECOMMEND_REQUEST = 189;
    private List<LocalMedia> mSelectList;
    private OnTakePhotoCallback mCallback;
    private boolean mIsCrop;
    private Disposable mDisposable;

    public interface OnTakePhotoCallback {
        void onTakePhoto(ArrayList<String> imgPaths);

        void onTakeCancel();
    }

    public static class SimpleOnTakePhotoCallback implements OnTakePhotoCallback {
        @Override
        public void onTakePhoto(ArrayList<String> imgPaths) {
        }

        @Override
        public void onTakeCancel() {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    /**
     * 选择图片-跳转去拍照选择
     */
    public void takeImageByCamera(OnTakePhotoCallback callback) {
        takeImageByCamera(callback, false);
    }

    public void takeImageByCamera(OnTakePhotoCallback callback, boolean isCrop) {
        this.mCallback = callback;
        this.mIsCrop = isCrop;
        runTaskOnStart(new LifecycleTask() {
            @Override
            public void execute(BaseDelegateFragment delegateFragment) {
                requestPermission(() -> {
                    configDefault(PictureSelector.create(TakePhotoDelegateFragment.this)
                            .openCamera(PictureMimeType.ofImage()), 1)
                            .isEnableCrop(isCrop)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                });
            }
        });
    }

    /**
     * 选择图片-跳转到相册
     */
    public void takeImageByGallery(OnTakePhotoCallback callback, int residueSelectPicCount) {
        takeImageByGallery(callback, residueSelectPicCount, false);
    }

    public void takeImageByGallery(OnTakePhotoCallback callback, int residueSelectPicCount, boolean isCrop) {
        this.mCallback = callback;
        this.mIsCrop = isCrop;
        runTaskOnStart(new LifecycleTask() {
            @Override
            public void execute(BaseDelegateFragment delegateFragment) {
                requestPermission(() -> {
                    configDefault(PictureSelector.create(TakePhotoDelegateFragment.this)
                            .openGallery(PictureMimeType.ofImage()), residueSelectPicCount)
                            .isEnableCrop(isCrop)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                });
            }
        });
    }

    /**
     * 选择视频-跳转去拍照选择
     */
    public void takeVideoByCamera(OnTakePhotoCallback callback) {
        this.mCallback = callback;
        runTaskOnStart(new LifecycleTask() {
            @Override
            public void execute(BaseDelegateFragment delegateFragment) {
                requestPermission(() -> {
                    configDefault(PictureSelector.create(TakePhotoDelegateFragment.this)
                            .openCamera(PictureMimeType.ofVideo()), 1)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                });
            }
        });
    }

    /**
     * 选择图片-跳转到相册
     */
    public void takeVideoByGallery(OnTakePhotoCallback callback, int residueSelectPicCount) {
        this.mCallback = callback;
        runTaskOnStart(new LifecycleTask() {
            @Override
            public void execute(BaseDelegateFragment delegateFragment) {
                requestPermission(() -> {
                    configDefault(PictureSelector.create(TakePhotoDelegateFragment.this)
                            .openGallery(PictureMimeType.ofVideo()), residueSelectPicCount)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                });
            }
        });
    }

    private PictureSelectionModel configDefault(PictureSelectionModel model, int residueSelectPicCount) {
        return model
                //预览图片时是否增强左右滑动图片体验
                .isPreviewEggs(true)
                //最少一张
                .minSelectNum(1)
                //最多选择多少张，外部传入，一般会动态改变
                .maxSelectNum(residueSelectPicCount)
                //多选
                .selectionMode(PictureConfig.MULTIPLE)
                //关闭原图选项
                .isOriginalImageControl(false)
                //裁剪比例
                .withAspectRatio(1, 1)
                //是否展示拍照图标
                .isCamera(true)
                //压缩
                .isCompress(true)
                //图片加载引擎
                .imageEngine(GlideEngine.createGlideEngine())
                //关于部分华为Android 10机型会出现转圈圈较长问题，2.5.1版本增加
                //通过利用图片加载框架的缓存方式获得该图片所在沙盒内的位置，而不在使用File Copy的方式拷贝至应用沙盒内
                //因为发现在华为10版本的系统上拷贝文件太频繁会导致io阻塞
                .loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine());
    }

    /**
     * 移动图片位置
     *
     * @param fromPosition 从哪里
     * @param toPosition   到哪里
     */
    public void movePhotoPosition(int fromPosition, int toPosition) {
        Collections.swap(mSelectList, fromPosition, toPosition);
    }

    /**
     * 跳转到预览
     *
     * @param position 预览第几张
     */
    public void startByPreview(final int position) {
        runTaskOnStart(new LifecycleTask() {
            @Override
            public void execute(BaseDelegateFragment delegateFragment) {
                PictureSelector.create(TakePhotoDelegateFragment.this)
                        .themeStyle(R.style.picture_default_style)
                        .openExternalPreview(position, mSelectList);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    mSelectList = PictureSelector.obtainMultipleResult(data);
                    ArrayList<String> imgPaths = new ArrayList<>();
                    for (LocalMedia localMedia : mSelectList) {
                        String path;
                        if (mIsCrop) {
                            path = localMedia.getCutPath();
                        } else {
                            //适配AndroidQ
                            if (SdkVersionUtils.checkedAndroid_Q()) {
                                path = localMedia.getAndroidQToPath();
                                if (path == null) {
                                    path = localMedia.getCompressPath();
                                }
                            } else {
                                path = localMedia.getCompressPath();
                            }
                            //这里拿不到，是视频的情况
                            if (path == null) {
                                path = localMedia.getRealPath();
                            }
                        }
                        imgPaths.add(path);
                    }
                    if (mCallback != null) {
                        mCallback.onTakePhoto(imgPaths);
                    }
                    break;
                case RECOMMEND_REQUEST:
                    if (data == null) {
                        if (mCallback != null) {
                            mCallback.onTakeCancel();
                        }
                        return;
                    }
                    ArrayList<String> imgPaths2 = data.getStringArrayListExtra(RECOMMEND_REQUEST_EXTRA);
                    if (imgPaths2 != null) {
                        if (mCallback != null) {
                            mCallback.onTakePhoto(imgPaths2);
                        }
                    } else {
                        if (mCallback != null) {
                            mCallback.onTakeCancel();
                        }
                    }
                default:
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (mCallback != null) {
                mCallback.onTakeCancel();
            }
        }
    }

    /**
     * 申请权限
     */
    @SuppressLint({"AutoDispose", "CheckResult"})
    private void requestPermission(Runnable runnable) {
        RxPermissions rxPermissions = new RxPermissions(this);
        mDisposable = rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        runnable.run();
                    }
                });
    }
}