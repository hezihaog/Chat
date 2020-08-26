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


/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib.utils <br>
 * <b>Create Date:</b> 2019/3/9  10:03 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 封装LoadingDialog <br>
 */
public class WaitLoadingController implements LifecycleObserver {
    private Activity mActivity;
    private LoadingDialog vLoadingDialog;
    private final Handler mMainHandler;

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
            if (vLoadingDialog != null && !vLoadingDialog.isShowing()) {
                vLoadingDialog.show();
            }
        });
    }

    public void hideWait() {
        ensureMainThreadRun(() -> {
            if (vLoadingDialog != null) {
                vLoadingDialog.dismiss();
            }
        });
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
}