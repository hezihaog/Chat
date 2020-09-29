package com.zh.android.base.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.alibaba.android.arouter.launcher.ARouter;
import com.zh.android.base.R;
import com.zh.android.base.core.LayoutCallback;


/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib.view <br>
 * <b>Create Date:</b> 2019/3/12  11:11 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 通用弹窗 <br>
 */
public abstract class BaseDialog extends Dialog implements LayoutCallback {
    /**
     * 内容View
     */
    private View vContentView;
    private LifecycleOwner mLifecycleOwner;

    public BaseDialog(@NonNull Context context, LifecycleOwner lifecycleOwner) {
        super(context, R.style.BaseDialog);
        mLifecycleOwner = lifecycleOwner;
        init();
    }

    public BaseDialog(@NonNull Context context, LifecycleOwner lifecycleOwner, int themeResId) {
        super(context, themeResId);
        mLifecycleOwner = lifecycleOwner;
        init();
    }

    private void init() {
        ARouter.getInstance().inject(this);
        onLayoutBefore();
        normalize();
        vContentView = getLayoutInflater().inflate(onInflaterViewId(), null);
        setContentView(vContentView);
        onInflaterViewAfter(vContentView);
        onBindView(vContentView);
        setData();
        //默认点击黑色背景不能关闭
        setCanceledOnTouchOutside(false);
    }

    /**
     * 正常化Dialog
     */
    private void normalize() {
        fixBackground();
        fixSize();
    }

    /**
     * 修复背景
     */
    private void fixBackground() {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        //去掉白色的背景
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * 修复大小
     */
    private void fixSize() {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        //获取需要显示的宽、高
        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int[] frameSize = onSetupDialogFrameSize(metrics.widthPixels, metrics.heightPixels);
        //去掉默认间距
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = frameSize[0];
        params.height = frameSize[1];
        window.setAttributes(params);
    }

    protected abstract int[] onSetupDialogFrameSize(int screenWidth, int screenHeight);

    @Override
    public void onLayoutBefore() {
    }

    @Override
    public void onInflaterViewAfter(View view) {
    }

    @Override
    public void onBindView(View view) {
    }

    @Override
    public void dismiss() {
        super.dismiss();
        onDismiss();
    }

    @Override
    public void setData() {
    }

    protected void onDismiss() {
    }

    public View getContentView() {
        return vContentView;
    }

    public Activity getActivity() {
        Context context = getContext();
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextThemeWrapper) {
            ContextThemeWrapper wrapper = (ContextThemeWrapper) context;
            if (wrapper.getBaseContext() instanceof Activity) {
                return (Activity) wrapper.getBaseContext();
            }
        }
        return null;
    }

    public LifecycleOwner getLifecycleOwner() {
        return mLifecycleOwner;
    }

    public void dismissDialog() {
        dismiss();
    }
}