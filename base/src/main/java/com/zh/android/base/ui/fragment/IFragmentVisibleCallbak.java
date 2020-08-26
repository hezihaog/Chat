package com.zh.android.base.ui.fragment;

/**
 * <b>Project:</b> ListRelated <br>
 * <b>Create Date:</b> 2017/1/10 <br>
 * <b>Author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> Fragment可见状态回调 <br>
 */
public interface IFragmentVisibleCallbak {
    /**
     * Fragment可见
     */
    void onFragmentVisible();

    /**
     * Fragment不可见
     */
    void onFragmentInvisible();
}