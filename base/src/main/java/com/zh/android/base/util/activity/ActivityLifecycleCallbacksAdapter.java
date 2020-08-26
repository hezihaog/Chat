package com.zh.android.base.util.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Package: me.wally.arch.activityprovider
 * FileName: ActivityLifecycleCallbacksAdapter
 * Date: on 2018/11/9  上午11:18
 * Auther: zihe
 * Descirbe:
 * Email: hezihao@linghit.com
 *
 * @author wally
 */
public class ActivityLifecycleCallbacksAdapter implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}