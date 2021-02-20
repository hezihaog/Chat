package com.zh.android.base.core;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import per.goweii.swipeback.SwipeBackDirection;
import per.goweii.swipeback.SwipeBackHelper;

/**
 * @author wally
 * @date 2021/02/20
 */
public class BaseSwipeBackActivity extends AppCompatActivity {
    protected SwipeBackHelper mSwipeBackHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSwipeBack();
    }

    /**
     * 初始化SwipeBackHelper
     */
    public void initSwipeBack() {
        if (mSwipeBackHelper == null) {
            mSwipeBackHelper = SwipeBackHelper.inject(this);
            mSwipeBackHelper.setSwipeBackEnable(swipeBackEnable());
            mSwipeBackHelper.setSwipeBackOnlyEdge(swipeBackOnlyEdge());
            mSwipeBackHelper.setSwipeBackForceEdge(swipeBackForceEdge());
            mSwipeBackHelper.setSwipeBackDirection(swipeBackDirection());
            mSwipeBackHelper.getSwipeBackLayout().setShadowStartColor(0);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSwipeBackHelper.onPostCreate();
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        mSwipeBackHelper.onEnterAnimationComplete();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSwipeBackHelper.onDestroy();
    }

    @Override
    public void finish() {
        if (mSwipeBackHelper.finish()) {
            super.finish();
        }
    }

    protected boolean swipeBackEnable() {
        return true;
    }

    protected boolean swipeBackOnlyEdge() {
        return false;
    }

    protected boolean swipeBackForceEdge() {
        return true;
    }

    @SwipeBackDirection
    protected int swipeBackDirection() {
        return SwipeBackDirection.FROM_LEFT;
    }
}