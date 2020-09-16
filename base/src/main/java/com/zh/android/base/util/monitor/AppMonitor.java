package com.zh.android.base.util.monitor;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib.utils <br>
 * <b>Create Date:</b> 2019-05-16  09:47 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> App前、后台监控 <br>
 */
public class AppMonitor {
    /**
     * 注册了的监听器
     */
    private List<Callback> mListener;
    /**
     * 注册了的过滤器
     */
    private List<Filter> mFilters;
    /**
     * 是否初始化了
     */
    private boolean isInited;
    /**
     * 活跃Activity的数量
     */
    private int mActiveActivityCount = 0;
    /**
     * 存活的Activity数量
     */
    private int mAliveActivityCount = 0;
    /**
     * 是否活跃，该标志位是为了过滤重复调用的问题
     */
    private boolean isActive;

    private AppMonitor() {
    }

    public static AppMonitor get() {
        return SingleHolder.INSTANCE;
    }

    private static final class SingleHolder {
        private static final AppMonitor INSTANCE = new AppMonitor();
    }

    public void initialize(Context context) {
        if (isInited) {
            return;
        }
        mListener = new CopyOnWriteArrayList<>();
        mFilters = new CopyOnWriteArrayList<>();
        registerLifecycle(context);
        isInited = true;
    }

    /**
     * 注册生命周期
     */
    private void registerLifecycle(Context context) {
        Application application = (Application) context.getApplicationContext();
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                boolean isIgnore = false;
                for (Filter filter : mFilters) {
                    if (filter.isIgnore(activity)) {
                        isIgnore = true;
                    }
                }
                if (isIgnore) {
                    return;
                }
                mAliveActivityCount++;
            }

            @Override
            public void onActivityStarted(Activity activity) {
                boolean isIgnore = false;
                for (Filter filter : mFilters) {
                    if (filter.isIgnore(activity)) {
                        isIgnore = true;
                    }
                }
                if (isIgnore) {
                    return;
                }
                mActiveActivityCount++;
                notifyChange();
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                boolean isIgnore = false;
                for (Filter filter : mFilters) {
                    if (filter.isIgnore(activity)) {
                        isIgnore = true;
                    }
                }
                if (isIgnore) {
                    return;
                }
                mActiveActivityCount--;
                notifyChange();
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                boolean isIgnore = false;
                for (Filter filter : mFilters) {
                    if (filter.isIgnore(activity)) {
                        isIgnore = true;
                    }
                }
                if (isIgnore) {
                    return;
                }
                mAliveActivityCount--;
                notifyAppAliveChange();
            }
        });
    }

    /**
     * 判断App是否活着
     */
    public boolean isAppAlive() {
        return mAliveActivityCount > 0;
    }

    /**
     * 判断App是否在前台
     */
    public boolean isAppForeground() {
        return mActiveActivityCount > 0;
    }

    /**
     * 判断App是否在后台
     */
    public boolean isAppBackground() {
        return mActiveActivityCount <= 0;
    }

    /**
     * 通知监听者
     */
    private void notifyChange() {
        if (mActiveActivityCount > 0) {
            if (!isActive) {
                for (Callback callback : mListener) {
                    callback.onAppForeground();
                }
                isActive = true;
            }
        } else {
            if (isActive) {
                for (Callback callback : mListener) {
                    callback.onAppBackground();
                }
                isActive = false;
            }
        }
    }

    /**
     * 通知监听者界面销毁
     */
    private void notifyAppAliveChange() {
        if (mAliveActivityCount == 0) {
            for (Callback callback : mListener) {
                callback.onAppUIDestroyed();
            }
            isActive = false;
        }
    }

    public interface Callback {
        /**
         * 当App切换到前台时回调
         */
        void onAppForeground();

        /**
         * App切换到后台时回调
         */
        void onAppBackground();

        /**
         * App所有界面都销毁了
         */
        void onAppUIDestroyed();
    }

    public static class CallbackAdapter implements Callback {

        @Override
        public void onAppForeground() {
        }

        @Override
        public void onAppBackground() {
        }

        @Override
        public void onAppUIDestroyed() {
        }
    }

    /**
     * 过滤器，用于让调用方决定，某种情况时，是否忽略某个Activity生命周期计数
     */
    public interface Filter {
        /**
         * 是否忽略
         *
         * @return 忽略返回true，不忽略返回false
         */
        boolean isIgnore(Activity activity);
    }

    /**
     * 注册过滤器
     */
    public void registerFilter(Filter filter) {
        if (mFilters.contains(filter)) {
            return;
        }
        mFilters.add(filter);
    }

    /**
     * 取消注册过滤器
     */
    public void unRegisterFilter(Filter filter) {
        mFilters.remove(filter);
    }

    /**
     * 注册回调
     */
    public void register(Callback callback) {
        if (mListener.contains(callback)) {
            return;
        }
        mListener.add(callback);
    }

    /**
     * 注销回调
     */
    public void unRegister(Callback callback) {
        if (!mListener.contains(callback)) {
            return;
        }
        mListener.remove(callback);
    }

    /**
     * 将栈顶activity移到前台
     */
    public void moveAppToForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (task.topActivity.getPackageName().equals(context.getPackageName())) {
                activityManager.moveTaskToFront(task.id, ActivityManager.MOVE_TASK_WITH_HOME);
            }
        }
    }
}