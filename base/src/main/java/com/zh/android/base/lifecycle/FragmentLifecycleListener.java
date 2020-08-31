package com.zh.android.base.lifecycle;

/**
 * Created by Hezihao on 2017/7/10.
 * Fragment生命周期回调接口
 */

public interface FragmentLifecycleListener extends LifecycleListener {
    /**
     * Fragment onAttach对应方法
     */
    void onAttach();

    /**
     * Fragment onStart对应方法
     */
    void onStart();

    /**
     * Fragment onResume
     */
    void onResume();

    /**
     * Fragment onPause
     */
    void onPause();

    /**
     * Fragment onStop对应方法
     */
    void onStop();

    /**
     * Fragment onDestroy对应方法
     */
    void onDestroy();

    /**
     * Fragment onDetach对应方法
     */
    void onDetach();
}
