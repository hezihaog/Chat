package com.zh.android.base.lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Hezihao on 2017/7/10.
 * Fragment使用的Lifecycle
 */

public class FragmentLifecycle implements Lifecycle<FragmentLifecycleListener> {
    /**
     * 读写分离，避免遍历的同时add进集合，抛出高并发异常。
     */
    private final CopyOnWriteArrayList<FragmentLifecycleListener> lifecycleListeners = new CopyOnWriteArrayList<FragmentLifecycleListener>();
    private boolean isAttach;
    private boolean isStarted;
    private boolean isResumed;
    private boolean isDestroyed;

    /**
     * 当完全具有焦点时设置为true，避免存在在onResume还没有执行完时，添加监听，造成onStop后续的生命周期被回调
     */
    private boolean isVisibleToUser = true;

    @Override
    public void addListener(FragmentLifecycleListener listener) {
        if (lifecycleListeners.contains(listener)) {
            return;
        }
        lifecycleListeners.add(listener);
        if (isAttach) {
            listener.onAttach();
        }
        if (isStarted) {
            listener.onStart();
        }
        if (isResumed) {
            listener.onResume();
        }
        if (!isVisibleToUser) {
            if (!isStarted) {
                listener.onStop();
            }
            if (!isResumed) {
                listener.onPause();
            }
            if (!isAttach) {
                listener.onDetach();
            }
            if (isDestroyed) {
                listener.onDestroy();
            }
        }
    }

    @Override
    public void removeListener(FragmentLifecycleListener listener) {
        if (lifecycleListeners.size() > 0 && lifecycleListeners.contains(listener)) {
            lifecycleListeners.remove(listener);
        }
    }

    @Override
    public void removeAllListener() {
        if (lifecycleListeners.size() > 0) {
            lifecycleListeners.clear();
        }
    }

    @Override
    public boolean containListener(FragmentLifecycleListener listener) {
        if (lifecycleListeners.size() <= 0) {
            return false;
        }
        return lifecycleListeners.contains(listener);
    }

    @Override
    public List<FragmentLifecycleListener> getAllListener() {
        ArrayList<FragmentLifecycleListener> list = new ArrayList<FragmentLifecycleListener>();
        list.addAll(lifecycleListeners);
        return list;
    }

    public void onAttach() {
        isAttach = true;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onAttach();
        }
    }

    public void onStart() {
        isStarted = true;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onStart();
        }
    }

    public void onResume() {
        isResumed = true;
        isVisibleToUser = true;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onResume();
        }
    }

    public void onPause() {
        isResumed = false;
        isVisibleToUser = false;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onPause();
        }
    }

    public void onStop() {
        isStarted = false;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onStop();
        }
    }

    public void onDestroy() {
        isDestroyed = true;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onDestroy();
        }
    }

    public void onDetach() {
        isAttach = false;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onDetach();
        }
    }
}
