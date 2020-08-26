package com.zh.android.base.core;

import android.view.View;

/**
 * <b>Project:</b> ListRelated <br>
 * <b>Create Date:</b> 2017/1/4 <br>
 * <b>Author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> UI界面加载以及事件 <br>
 */
public interface LayoutCallback {
    /**
     * 设置布局之前回调
     */
    void onLayoutBefore();

    /**
     * 获取布局LayoutId
     */
    int onInflaterViewId();

    /**
     * 填充完毕View后回调
     */
    void onInflaterViewAfter(View view);

    /**
     * 查找View和给View进行相关设置等
     */
    void onBindView(View view);

    /**
     * 设置数据
     */
    void setData();
}