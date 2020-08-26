package com.zh.android.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zh.android.base.R;


/**
 * <b>Package:</b> com.hule.dashi.answer.widget <br>
 * <b>Create Date:</b> 2019/1/16  9:43 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 自定义单选按钮 <br>
 */
public class LingJiRadioButton extends FrameLayout {
    private boolean mIsChecked = false;
    private OnClickChatRadioButtonListener mOnClickChatRadioButtonListener;
    private OnCheckedStatusChangeListener mOnCheckedStatusChangeListener;

    public LingJiRadioButton(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public LingJiRadioButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public LingJiRadioButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(R.styleable.LingJiRadioButton);
            mIsChecked = typedArray.getBoolean(R.styleable.LingJiRadioButton_crb_checked, false);
            typedArray.recycle();
        }
        //设置一个点击事件监听，是为了能让OnClickCustomRadioButtonListener能够响应
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public void setChecked(boolean checked) {
        mIsChecked = checked;
        toggleCheckedStatus(mIsChecked);
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    /**
     * 切换选中颜色和非选中颜色
     *
     * @param isChecked 是否选中
     */
    private void toggleCheckedStatus(boolean isChecked) {
        //切换状态
        if (mOnCheckedStatusChangeListener != null) {
            mOnCheckedStatusChangeListener.onCheckedStatusChangeListener(this, isChecked);
        }
    }

    private class OnClickWrapper implements OnClickListener {
        private OnClickListener mTargetListener;

        public OnClickWrapper(OnClickListener targetListener) {
            this.mTargetListener = targetListener;
        }

        @Override
        public void onClick(View view) {
            OnClickChatRadioButtonListener onClickChatRadioButtonListener = getOnClickChatRadioButtonListener();
            if (onClickChatRadioButtonListener != null) {
                onClickChatRadioButtonListener.onClickCustomRadioButton(getSelf());
            }
            if (mTargetListener != null) {
                mTargetListener.onClick(view);
            }
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener onClickListener) {
        super.setOnClickListener(new OnClickWrapper(onClickListener));
    }

    /**
     * 为了避免使用方设置OnClick监听替换掉CustomRadioGroup中设置的监听，另外提供一个接口
     * 在设置点击事件的时候也会转调该监听
     */
    public interface OnClickChatRadioButtonListener {
        void onClickCustomRadioButton(LingJiRadioButton button);
    }

    public void setOnClickChatRadioButtonListener(OnClickChatRadioButtonListener onClickChatRadioButtonListener) {
        this.mOnClickChatRadioButtonListener = onClickChatRadioButtonListener;
    }

    public OnClickChatRadioButtonListener getOnClickChatRadioButtonListener() {
        return this.mOnClickChatRadioButtonListener;
    }

    /**
     * 切换状态的监听，切换图标或者颜色，或者是着色，还是去红点等操作，在该回调中做！
     */
    public interface OnCheckedStatusChangeListener {
        /**
         * 当切换选中时回调
         */
        void onCheckedStatusChangeListener(LingJiRadioButton button, boolean isChecked);
    }

    public void setOnCheckedStatusChangeListener(OnCheckedStatusChangeListener onCheckedStatusChangeListener) {
        this.mOnCheckedStatusChangeListener = onCheckedStatusChangeListener;
    }

    public OnCheckedStatusChangeListener getOnCheckedStatusChangeListener() {
        return mOnCheckedStatusChangeListener;
    }

    public LingJiRadioButton getSelf() {
        return this;
    }
}