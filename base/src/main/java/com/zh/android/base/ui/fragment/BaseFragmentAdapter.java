package com.zh.android.base.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * <b>Project:</b> ListRelated <br>
 * <b>Create Date:</b> 2017/1/7 <br>
 * <b>Author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> Fragment的adapter <br>
 */
public class BaseFragmentAdapter extends FragmentPagerAdapter {
    protected Context mContext;
    protected ArrayList<TabInFo> mTabInFos;
    protected SparseArray<Fragment> mFragments;
    protected FragmentManager mFragmentManager;
    private InstantiateFragmentCallback mCallback;

    public static class TabInFo {
        private final String tag; // tag
        private final String clss; // 类
        private final Bundle args; // 参数
        private final String title; // 标题

        public TabInFo(String _tag, String _class, Bundle _args, String _title) {
            tag = _tag;
            clss = _class;
            args = _args;
            title = _title;
        }

        public String getTag() {
            return tag;
        }

        public String getClss() {
            return clss;
        }

        public Bundle getArgs() {
            return args;
        }

        public String getTitle() {
            return title;
        }

    }

    public BaseFragmentAdapter(FragmentManager fm, Context context) {
        this(fm, context, null);
    }

    public BaseFragmentAdapter(FragmentManager fm, Context context, InstantiateFragmentCallback callback) {
        super(fm);
        mContext = context;
        mFragmentManager = fm;
        mTabInFos = new ArrayList<>();
        mFragments = new SparseArray<>();
        mCallback = callback;
    }

    /**
     * Fragment被实例化时回调
     */
    public interface InstantiateFragmentCallback {
        void onInstantiate(Fragment fragment, int position);
    }

    /**
     * 获取对应位置的Fragment的tag，名称
     *
     * @param position Fragment所在的位置
     * @return
     */
    public String getFragmentName(int position) {
        return mTabInFos.get(position).getTag();
    }

    /**
     * 添加一个Fragment
     */
    public void addFragment(TabInFo info) {
        mTabInFos.add(info);
        notifyDataSetChanged();
    }

    /**
     * 添加一个Fragment
     *
     * @param tag   tag，名称
     * @param clss  类
     * @param args  参数
     * @param title 标题
     */
    public void addFragment(String tag, String clss, Bundle args, String title) {
        TabInFo info = new TabInFo(tag, clss, args, title);
        mTabInFos.add(info);
        notifyDataSetChanged();
    }

    /**
     * 获取Fragment
     *
     * @param position 位置
     */
    public Fragment getFragment(int position) {
        String tag = getFragmentName(position);
        Fragment fragment = mFragmentManager.
                findFragmentByTag(tag);
        if (fragment == null) {
            return getItem(position);
        }
        return fragment;
    }

    @Override
    public Fragment getItem(int position) {
        TabInFo info = mTabInFos.get(position);
        Fragment fragment = mFragments.get(position);
        if (fragment == null) {
            fragment = Fragment.instantiate(mContext, info.getClss(), info.getArgs());
            if (mCallback != null) {
                mCallback.onInstantiate(fragment, position);
            }
            mFragments.put(position, fragment);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mTabInFos.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabInFos.get(position).getTitle();
    }

    /**
     * 为Fragment生成tag，名称
     *
     * @param viewId ViewPager的id
     * @param id     位置，当前的Fragment所在的position位置
     */
    public static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}