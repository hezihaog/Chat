package com.zh.android.base.ui.dialog.bottomsheet;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zh.android.base.R;
import com.zh.android.base.ui.dialog.BaseDialog;


/**
 * <b>Package:</b> com.hule.dashi.live.room.ui.dialog <br>
 * <b>Create Date:</b> 2019-05-08  17:27 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 通用底部弹出Dialog，只定义动画和位置，内容子类去定义 <br>
 */
public abstract class BaseBottomSheetDialog extends BaseDialog {
    private BottomSheetBehavior<View> mBottomSheetBehavior;

    public BaseBottomSheetDialog(@NonNull Context context, LifecycleOwner lifecycleOwner) {
        super(context, lifecycleOwner, R.style.base_bottom_sheet_dialog_style);
        //设置外层阴影可点击消失
        setCanceledOnTouchOutside(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置弹窗在底部
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    protected int[] onSetupDialogFrameSize(int screenWidth, int screenHeight) {
        int[] size = new int[2];
        size[0] = screenWidth;
        size[1] = ViewGroup.LayoutParams.WRAP_CONTENT;
        return size;
    }

    @Override
    public int onInflaterViewId() {
        return R.layout.base_bottom_sheet_dialog;
    }

    /**
     * 通知创建内容View
     *
     * @param inflater 填充工厂
     * @param parent   容器View
     */
    protected abstract View onCreateContentView(LayoutInflater inflater, ViewGroup parent);

    @CallSuper
    @Override
    public void onBindView(View view) {
        CoordinatorLayout container = view.findViewById(R.id.content_container);
        View contentView = onCreateContentView(getLayoutInflater(), container);
        //下拉关闭
        mBottomSheetBehavior = BottomSheetBehavior.from(contentView);
        mBottomSheetBehavior.setHideable(true);
        //设置展开高度，设置为屏幕的四分之三，这样才不会分2段展开，而是一段
        int screenHeight = getScreenHeight(view.getContext());
        mBottomSheetBehavior.setPeekHeight((screenHeight * 3) / 4);
        //设置状态回调
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    cancel();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        //添加进布局
        container.addView(contentView);
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setHideable(cancelable);
        }
    }

    @Override
    public void show() {
        super.show();
        animationShow();
    }

    @Override
    public void dismiss() {
        //这里将dismiss覆盖掉，不调用super，让动画结束后再调用callDismiss去调用dismiss
        animationHide();
    }

    /**
     * 调用真正的dismiss
     */
    private void callDismiss() {
        super.dismiss();
    }

    /**
     * 从底部向上滑动
     */
    private void animationShow() {
        if ((getContentView()) == null) {
            return;
        }
        TranslateAnimation animation = new TranslateAnimation(1, 0.0F,
                1, 0.0F, 1, 1.0F, 1, 0.0F);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(180L);
        animation.setFillAfter(true);
        animation.setAnimationListener(new AnimationAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                if (mBottomSheetBehavior != null && mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        getContentView().startAnimation(animation);
    }

    /**
     * 向底部滑动
     */
    private void animationHide() {
        if (getContentView() == null) {
            return;
        }
        TranslateAnimation animation = new TranslateAnimation(1, 0.0F,
                1, 0.0F, 1, 0.0F, 1, 1.0F);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(180L);
        animation.setFillAfter(true);
        animation.setAnimationListener(new AnimationAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束，关闭弹窗
                callDismiss();
            }
        });
        getContentView().startAnimation(animation);
    }

    private static class AnimationAdapter implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static int getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }
}