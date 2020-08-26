package com.zh.android.base.util.listener;

import android.view.View;

/**
 * <b>Package:</b> com.zh.touchassistant <br>
 * <b>FileName:</b> DelayOnClickListener <br>
 * <b>Create Date:</b> 2018/12/13  下午10:45 <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b> 用于点击事件防重点、防暴击，设置点击事件，设置该类，实现抽象方法 <br>
 */
public class DelayOnClickListener implements View.OnClickListener {
    /**
     * 默认延时时间
     */
    private static final int DEFAULT_DELAY_TIME = 300;
    /**
     * 上一次的点击时间
     */
    private long mLastClickTime;
    /**
     * 延迟时间
     */
    private int mDelayTime;
    /**
     * 包裹的监听器
     */
    private View.OnClickListener mWrapperListener;

    public DelayOnClickListener(View.OnClickListener wrapperListener) {
        this(DEFAULT_DELAY_TIME, wrapperListener);
    }

    public DelayOnClickListener(int delayTime, View.OnClickListener wrapperListener) {
        if (delayTime < 0) {
            return;
        }
        mDelayTime = delayTime;
        mWrapperListener = wrapperListener;
    }

    @Override
    public final void onClick(View view) {
        if (System.currentTimeMillis() - mLastClickTime < mDelayTime) {
            return;
        }
        if (mWrapperListener != null) {
            mWrapperListener.onClick(view);
        }
        mLastClickTime = System.currentTimeMillis();
    }
}