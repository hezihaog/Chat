package com.zh.android.base.util.loading;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.zh.android.base.util.SoftKeyBoardUtil;


/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib.utils <br>
 * <b>Create Date:</b> 2019/3/9  10:03 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 封装LoadingDialog <br>
 */
public class WaitLoadingController implements LifecycleObserver {
    /**
     * 一定要显示够指定时间
     */
    private static final long LOAD_MIN_TIME = 100;

    private Activity mActivity;
    private LoadingDialog vLoadingDialog;
    private final Handler mMainHandler;
    /**
     * 开始展示的时间
     */
    private long showTime;

    public WaitLoadingController(@NonNull Activity activity, LifecycleOwner lifecycleOwner) {
        mActivity = activity;
        mMainHandler = new Handler(Looper.getMainLooper());
        lifecycleOwner.getLifecycle().addObserver(this);
        setup();
        setCanceledOnTouchOutside(false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onLifecycleDestroy() {
        destroy();
    }

    private Context getContext() {
        return mActivity;
    }

    private Activity getActivity() {
        return mActivity;
    }

    public void setCancelable(boolean cancelable) {
        if (vLoadingDialog != null) {
            vLoadingDialog.setCancelable(cancelable);
        }
    }

    public void setCanceledOnTouchOutside(boolean isCanTouchOutside) {
        if (vLoadingDialog != null) {
            vLoadingDialog.setCanceledOnTouchOutside(isCanTouchOutside);
        }
    }

    private void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        if (vLoadingDialog != null) {
            vLoadingDialog.setOnCancelListener(listener);
        }
    }

    /**
     * 设置被取消时，关闭Activity
     */
    public void setOnCancelFinishActivity() {
        setCanOnCancelFinishActivity(true);
    }

    /**
     * 设置是否可以取消弹窗时，关闭界面
     */
    public void setCanOnCancelFinishActivity(boolean isCanCancelFinish) {
        if (isCanCancelFinish) {
            setOnCancelListener(dialog -> {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            });
        } else {
            setOnCancelListener(null);
        }
    }

    private void setup() {
        ensureMainThreadRun(() -> {
            vLoadingDialog = new LoadingDialog(getActivity());
            vLoadingDialog.setCanceledOnTouchOutside(true);
            vLoadingDialog.setCancelable(true);
        });
    }

    public void showWait() {
        ensureMainThreadRun(() -> {
            //隐藏输入法
            SoftKeyBoardUtil.hideKeyboard(mActivity.findViewById(android.R.id.content));
            if (vLoadingDialog != null && !vLoadingDialog.isShowing()) {
                showTime = System.currentTimeMillis();
                vLoadingDialog.show();
            }
        });
    }

    public void hideWait() {
        long currentTime = System.currentTimeMillis();
        Runnable runnable = () -> {
            if (vLoadingDialog != null) {
                vLoadingDialog.dismiss();
            }
        };
        //显示到调用结束，间隔的时间
        long intervalTime = currentTime - showTime;
        //最少要显示够的时间
        long leastTime = showTime + LOAD_MIN_TIME;
        //如果显示时间比最大时间大，则马上隐藏
        if (intervalTime >= LOAD_MIN_TIME) {
            ensureMainThreadRun(runnable);
        } else {
            //否则，补够时间，再隐藏
            ensureMainThreadRun(runnable, leastTime - currentTime);
        }
    }

    private void destroy() {
        ensureMainThreadRun(() -> {
            hideWait();
            vLoadingDialog = null;
            mActivity = null;
        });
    }

    /**
     * 确保主线程执行
     */
    public void ensureMainThreadRun(Runnable runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            mMainHandler.post(runnable);
        }
    }

    /**
     * 延时执行，确保在主线程
     */
    public void ensureMainThreadRun(Runnable runnable, long delayMillis) {
        mMainHandler.postDelayed(runnable, delayMillis);
    }
}