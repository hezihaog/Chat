package com.zh.android.base.lifecycle;

/**
 * Package: com.hzh.lifecycle.dispatch.base
 * FileName: IDelegateFragment
 * Date: on 2018/2/16  下午10:53
 * Auther: zihe
 * Descirbe:
 * Email: hezihao@linghit.com
 */

public interface IDelegateFragment {
    enum Status {
        /**
         * 生命周期状态
         */
        ATTACH(),
        START(),
        RESUME(),
        PAUSE(),
        STOP(),
        DESTROY(),
        DETACH()
    }

    interface LifecycleTask {
        void execute(BaseDelegateFragment delegateFragment);
    }

    /**
     * 添加一个任务，在onStart时执行
     */
    IDelegateFragment runTaskOnStart(final LifecycleTask task);

    IDelegateFragment runTaskOnResume(final LifecycleTask task);

    IDelegateFragment runTaskOnResume(final LifecycleTask task, long delayMillis);

    IDelegateFragment runTaskOnPause(final LifecycleTask task);

    IDelegateFragment runTaskOnStop(final LifecycleTask task);

    IDelegateFragment runTaskOnDestroy(final LifecycleTask task);

    IDelegateFragment runTaskOnDetach(final LifecycleTask task);

    /**
     * 添加一个任务在指定的生命周期中
     */
    IDelegateFragment runTaskInLifecycle(final LifecycleTask task, final Status status);

    /**
     * 延时执行任务
     */
    IDelegateFragment runTaskInLifecycle(final LifecycleTask task, final Status status, long delayMillis);

    /**
     * 弹出所有队列中的任务
     */
    void detachAndPopAllTask();

    /**
     * 弹出栈中的所有任务
     */
    void popAllTask();

    /**
     * 运行在主线程
     */
    IDelegateFragment runOnUiThread(Runnable action);

    /**
     * 延迟一定毫秒的任务在主线程
     */
    IDelegateFragment runOnUiThread(Runnable action, long delayMillis);
}