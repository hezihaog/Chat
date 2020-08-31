package com.zh.android.base.lifecycle;

import android.os.Handler;
import android.os.Looper;

import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * Package: com.hzh.lifecycle.dispatch
 * FileName: DelegateFinder
 * Date: on 2018/2/17  下午3:42
 * Auther: zihe
 * Descirbe:代理查找者
 * Email: hezihao@linghit.com
 */

public class DelegateFragmentFinder {
    private static final String DELEGATE_FRAGMENT_TAG_PREFIX = "tag_delegate_fragment:";
    private static final int ID_REMOVE_FRAGMENT_MANAGER = 1;

    private final Handler handler;
    private final ArrayMap<FragmentManager, ArrayList<Wrapper>> pendingFragments = new ArrayMap<>();

    private static class Wrapper {
        private String mTag;
        private BaseDelegateFragment mDelegateFragment;

        public Wrapper(String tag, BaseDelegateFragment delegateFragment) {
            mTag = tag;
            mDelegateFragment = delegateFragment;
        }
    }

    private DelegateFragmentFinder() {
        handler = new Handler(Looper.getMainLooper());
    }

    private static class Singleton {
        private static final DelegateFragmentFinder INSTANCE = new DelegateFragmentFinder();
    }

    public static DelegateFragmentFinder getInstance() {
        return Singleton.INSTANCE;
    }

    private String buildFragmentTag(Class fragmentClazz) {
        return DELEGATE_FRAGMENT_TAG_PREFIX + fragmentClazz.getName();
    }

    /**
     * 添加代理fragment到Activity
     */
    public <T extends BaseDelegateFragment> T find(FragmentActivity activity, Class<T> delegateClass) {
        return findByFragmentManager(activity.getSupportFragmentManager(), delegateClass);
    }

    /**
     * 添加代理fragment到fragment
     */
    public <T extends BaseDelegateFragment> T find(Fragment fragment, Class<T> delegateClass) {
        return findByFragmentManager(fragment.getChildFragmentManager(), delegateClass);
    }

    public <T extends BaseDelegateFragment> T findByFragmentManager(FragmentManager fm, Class<T> delegateClass) {
        String fragmentTag = buildFragmentTag(delegateClass);
        BaseDelegateFragment current = (BaseDelegateFragment) fm.findFragmentByTag(fragmentTag);
        if (current == null) {
            //这里的缓存Fragment是由于fragment的事务机制，和Looper机制，
            // 当2次执行到该代码段时，beginTransaction可能还在执行完，findFragmentByTag时找不到的，
            // 这就有可能创建了多个frag的实例，所以加多一个缓存实例，当真正执行完beginTransaction，再将缓存移除。
            ArrayList<Wrapper> wrappers = pendingFragments.get(fm);
            if (wrappers != null) {
                for (Wrapper wrapper : wrappers) {
                    if (fragmentTag.equals(wrapper.mTag)) {
                        current = wrapper.mDelegateFragment;
                        break;
                    }
                }
            }
            if (current == null) {
                current = buildDelegate(delegateClass);
                ArrayList<Wrapper> wrapperList = pendingFragments.get(fm);
                if (wrapperList == null) {
                    wrapperList = new ArrayList<>();
                }
                wrapperList.add(new Wrapper(fragmentTag, current));
                pendingFragments.put(fm, wrapperList);
                fm.beginTransaction().add(current, DELEGATE_FRAGMENT_TAG_PREFIX).commitAllowingStateLoss();
                current.runTaskOnDestroy(new IDelegateFragment.LifecycleTask() {
                    @Override
                    public void execute(BaseDelegateFragment delegateFragment) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<Wrapper> wrappers = pendingFragments.get(fm);
                                if (wrappers != null) {
                                    Wrapper needRemoveTarget = null;
                                    for (Wrapper wrapper : wrappers) {
                                        if (wrapper.mDelegateFragment == delegateFragment) {
                                            needRemoveTarget = wrapper;
                                        }
                                    }
                                    wrappers.remove(needRemoveTarget);
                                    if (wrappers.size() == 0) {
                                        pendingFragments.remove(fm);
                                    }
                                }
                            }
                        });
                        handler.obtainMessage(ID_REMOVE_FRAGMENT_MANAGER, fm).sendToTarget();
                    }
                });
            }
        }
        return (T) current;
    }

    /**
     * 构造代理fragment
     *
     * @param delegateClass 代理fragment的class
     */
    private BaseDelegateFragment buildDelegate(Class<? extends BaseDelegateFragment> delegateClass) {
        if (delegateClass == null) {
            throw new NullPointerException("delegateClass must be not null");
        }
        BaseDelegateFragment delegateFragment = null;
        try {
            Constructor<? extends BaseDelegateFragment> constructor = delegateClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            delegateFragment = constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delegateFragment;
    }
}