package com.zh.android.circle.mall.ui.widget.webview;

import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import androidx.fragment.app.FragmentActivity;

import com.draggable.library.extension.ImageViewerHelper;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author wally
 * @date 2020/10/27
 * 商城网页JS对象
 */
public class MallWebJsHandler implements Serializable {
    private static final long serialVersionUID = 2666362941056397976L;

    private final FragmentActivity activity;
    private final Callback callback;
    private final Handler mMainHandler;

    /**
     * 回调，当Web端调用，我们给提供的Api时，回调外部处理
     */
    public interface Callback {
        void injectContent();
    }

    public MallWebJsHandler(FragmentActivity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
        this.mMainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 提供给Web端的API
     */
    @JavascriptInterface
    public void injectContent() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.injectContent();
                }
            }
        });
    }

    /**
     * 预览图片
     *
     * @param url 图片地址
     */
    @JavascriptInterface
    public void showImage(String url) {
        List<String> imageList = Collections.singletonList(url);
        ImageViewerHelper.INSTANCE.showImages(activity, imageList, 0, true);
    }

    /**
     * 主线程执行
     */
    private void runOnUiThread(Runnable runnable) {
        mMainHandler.post(runnable);
    }
}