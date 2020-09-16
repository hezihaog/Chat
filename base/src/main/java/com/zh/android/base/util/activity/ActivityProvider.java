package com.zh.android.base.util.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.Nullable;


import com.zh.android.contextprovider.ContextProvider;

import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Package: me.wally.lvjiguide.provider
 * FileName: ActivityProvider
 * Date: on 2018/9/26  上午10:41
 * Auther: zihe
 * Descirbe:
 * Email: hezihao@linghit.com
 */
public class ActivityProvider {
    private ActivityStackManager mActivityStackManager;
    private CopyOnWriteArrayList<ActivityLifecycleCallbacksAdapter> mCallbacksAdapters;

    private ActivityProvider() {
        ensureInit();
        //全局注册Activity
        Application application = ContextProvider.get().getApplication();
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksAdapter() {
            @Override
            public void onActivityCreated(@Nullable Activity activity, @Nullable Bundle savedInstanceState) {
                super.onActivityCreated(activity, savedInstanceState);
                mActivityStackManager.addActivity(activity);
                for (ActivityLifecycleCallbacksAdapter adapter : mCallbacksAdapters) {
                    adapter.onActivityCreated(activity, savedInstanceState);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                super.onActivityStarted(activity);
                for (ActivityLifecycleCallbacksAdapter adapter : mCallbacksAdapters) {
                    adapter.onActivityStarted(activity);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                super.onActivityResumed(activity);
                for (ActivityLifecycleCallbacksAdapter adapter : mCallbacksAdapters) {
                    adapter.onActivityResumed(activity);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                super.onActivityPaused(activity);
                for (ActivityLifecycleCallbacksAdapter adapter : mCallbacksAdapters) {
                    adapter.onActivityPaused(activity);
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
                super.onActivityStopped(activity);
                for (ActivityLifecycleCallbacksAdapter adapter : mCallbacksAdapters) {
                    adapter.onActivityStopped(activity);
                }
            }

            @Override
            public void onActivityDestroyed(@Nullable Activity activity) {
                super.onActivityDestroyed(activity);
                mActivityStackManager.removeActivity(activity);
                for (ActivityLifecycleCallbacksAdapter adapter : mCallbacksAdapters) {
                    adapter.onActivityDestroyed(activity);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                super.onActivitySaveInstanceState(activity, outState);
                for (ActivityLifecycleCallbacksAdapter adapter : mCallbacksAdapters) {
                    adapter.onActivitySaveInstanceState(activity, outState);
                }
            }
        });
    }

    private static final class SingleHolder {
        private static final ActivityProvider INSTANCE = new ActivityProvider();
    }

    public static ActivityProvider initialize() {
        return get();
    }

    public static ActivityProvider get() {
        return SingleHolder.INSTANCE;
    }

    private ActivityStackManager ensureInit() {
        if (this.mActivityStackManager == null) {
            this.mActivityStackManager = ActivityStackManager.getAppManager();
        }
        if (mCallbacksAdapters == null) {
            mCallbacksAdapters = new CopyOnWriteArrayList<>();
        }
        return mActivityStackManager;
    }

    public Activity getActivity(Class<?> clazz) {
        ensureInit();
        return mActivityStackManager.getActivity(clazz);
    }

    public Activity getCurrentActivity() {
        ensureInit();
        return mActivityStackManager.getCurrentActivity();
    }

    public Activity getFirstActivity() {
        ensureInit();
        return mActivityStackManager.getFirstActivity();
    }

    public LinkedList<Activity> getAllActivity() {
        return new LinkedList<>(mActivityStackManager.getActivityStack());
    }

    public void registerLifecycleCallback(ActivityLifecycleCallbacksAdapter callback) {
        if (!mCallbacksAdapters.contains(callback)) {
            mCallbacksAdapters.add(callback);
        }
    }

    public void unregisterLifecycleCallback(ActivityLifecycleCallbacksAdapter callback) {
        mCallbacksAdapters.remove(callback);
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        ensureInit();
        mActivityStackManager.finishAllActivity();
    }
}