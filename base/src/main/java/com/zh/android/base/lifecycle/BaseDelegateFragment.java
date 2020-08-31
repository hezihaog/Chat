package com.zh.android.base.lifecycle;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Package: com.hzh.lifecycle.dispatch
 * FileName: DelegateFragment
 * Date: on 2018/2/16  下午10:33
 * Auther: zihe
 * Descirbe:委托Fragment类
 * Email: hezihao@linghit.com
 */

public class BaseDelegateFragment extends LifecycleFragment implements IDelegateFragment {
    private ConcurrentHashMap<Integer, LifecycleTask> callbacks = new ConcurrentHashMap<>();
    private Handler mMainHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        detachAndPopAllTask();
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public IDelegateFragment runTaskOnStart(final LifecycleTask task) {
        runTaskInLifecycle(task, Status.START);
        return this;
    }

    @Override
    public IDelegateFragment runTaskOnResume(final LifecycleTask task) {
        runTaskOnResume(task, 0);
        return this;
    }

    @Override
    public IDelegateFragment runTaskOnResume(LifecycleTask task, long delayMillis) {
        runTaskInLifecycle(task, Status.RESUME, delayMillis);
        return this;
    }

    @Override
    public IDelegateFragment runTaskOnPause(final LifecycleTask task) {
        runTaskInLifecycle(task, Status.PAUSE);
        return this;
    }

    @Override
    public IDelegateFragment runTaskOnStop(final LifecycleTask task) {
        runTaskInLifecycle(task, Status.STOP);
        return this;
    }

    @Override
    public IDelegateFragment runTaskOnDestroy(final LifecycleTask task) {
        runTaskInLifecycle(task, Status.DESTROY);
        return this;
    }

    @Override
    public IDelegateFragment runTaskOnDetach(final LifecycleTask task) {
        runTaskInLifecycle(task, Status.DETACH);
        return this;
    }

    @Override
    public IDelegateFragment runTaskInLifecycle(final LifecycleTask task, final Status status) {
        return runTaskInLifecycle(task, status, 0);
    }

    @Override
    public IDelegateFragment runTaskInLifecycle(LifecycleTask task, Status status, long delayMillis) {
        Runnable taskRunnable = new Runnable() {
            @Override
            public void run() {
                callbacks.put(task.hashCode(), task);
                getProxyLifecycle().addListener(new SimpleFragmentLifecycleAdapter() {
                    @Override
                    public void onAttach() {
                        super.onAttach();
                        if (status.ordinal() == Status.ATTACH.ordinal()) {
                            runNow();
                        }
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        if (status.ordinal() == Status.START.ordinal()) {
                            runNow();
                        }
                    }

                    @Override
                    public void onResume() {
                        super.onResume();
                        if (status.ordinal() == Status.RESUME.ordinal()) {
                            runNow();
                        }
                    }

                    @Override
                    public void onPause() {
                        super.onPause();
                        if (status.ordinal() == Status.PAUSE.ordinal()) {
                            runNow();
                        }
                    }

                    @Override
                    public void onStop() {
                        super.onStop();
                        if (status.ordinal() == Status.STOP.ordinal()) {
                            runNow();
                        }
                    }

                    @Override
                    public void onDestroy() {
                        super.onDestroy();
                        if (status.ordinal() == Status.DESTROY.ordinal()) {
                            runNow();
                        }
                    }

                    @Override
                    public void onDetach() {
                        super.onDetach();
                        if (status.ordinal() == Status.DESTROY.ordinal()) {
                            runNow();
                        }
                    }

                    /**
                     * 马上执行，并且移除监听，就是说例如即使下一次的onResume再次回调，也不会重复执行
                     */
                    private void runNow() {
                        callbacks.get(task.hashCode()).execute(BaseDelegateFragment.this);
                        getProxyLifecycle().removeListener(this);
                    }
                });
            }
        };
        if (delayMillis <= 0) {
            taskRunnable.run();
        } else {
            mMainHandler.postDelayed(taskRunnable, delayMillis);
        }
        return this;
    }

    /**
     * 移除所有callback
     */
    @Override
    public void popAllTask() {
        if (callbacks != null && callbacks.size() > 0) {
            callbacks.clear();
        }
    }

    /**
     * 获取主线程的Handler
     */
    public Handler getMainHandler() {
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }
        return mMainHandler;
    }

    @Override
    public void detachAndPopAllTask() {
        popAllTask();
        getProxyLifecycle().removeAllListener();
    }

    @Override
    public IDelegateFragment runOnUiThread(Runnable action) {
        runOnUiThread(action, -1);
        return this;
    }

    @Override
    public IDelegateFragment runOnUiThread(Runnable action, long delayMillis) {
        if (delayMillis <= 0) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mMainHandler.post(action);
            } else {
                action.run();
            }
        } else {
            mMainHandler.postDelayed(action, delayMillis);
        }
        return this;
    }
}