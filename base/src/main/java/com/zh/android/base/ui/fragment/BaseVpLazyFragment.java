package com.zh.android.base.ui.fragment;


import com.zh.android.base.core.BaseFragment;
import com.zh.android.base.core.LayoutCallback;

/**
 * <b>Project:</b> ListRelated <br>
 * <b>Create Date:</b> 2017/1/4 <br>
 * <b>Author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> BaseLazyFragment懒加载Fragment，这个的用途是配合ViewPager来使用 <br>
 * <p>
 * 注意:如果是普通的不是放在ViewPager中的Fragment的话，用这个不行，需要使用onHiddenChanged方法
 * <p>
 * 总的来说，这个类就是为ViewPager里面使用Fragment而生的
 */
public abstract class BaseVpLazyFragment extends BaseFragment
        implements IFragmentVisibleCallbak, LayoutCallback {
    //    /**
//     * 分别表示当前Fragment是否可见,是否已准备(表示已经走过onCreateView方法)以及是否数据已加载,
//     * 这里需要设置为true，因为在不使用ViewPager的情况下，会出问题，其次setUserVisibleHint的调用比onViewCreated早
//     */
//    protected boolean isVisible = true;
//    protected boolean isPrepared = false;
//    protected boolean isLoaded = false;
//
//    private View mView;
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mView = view;
//        //只要走过这个方法就认为已经加载了根View了
//        isPrepared = true;
//        //如果可见说明这个Fragment初次加载就是可见，应立即初始化布局
//        if (isVisible) {
//            onBindView(mView);
//            setData();
//            isLoaded = true;
//        }
//    }
//
//    @Override
//    public void onFragmentVisible() {
//    }
//
//    @Override
//    public void onFragmentInvisible() {
//    }
//
//    @Override
//    public void onLoadedData() {
//        if (!isPrepared) {
//            return;
//        }
//        //只有没有加载才去初始化View和绑定数据
//        if (!isLoaded) {
//            onBindView(mView);
//            setData();
//            isLoaded = true;
//        }
//    }
//
//    /**
//     * 不提供覆写，需监听可见性的子类可覆写{@link #onFragmentVisible()}和{@link #onFragmentInvisible()}方法
//     *
//     * @param isVisibleToUser 当前Fragment的可见性，onCreateView之前调用，如果当前Fragment没有放在Vp里面，这个方法根本不会调用的
//     */
//    @Override
//    public final void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        isVisible = isVisibleToUser;
//        if (getUserVisibleHint()) {
//            onLoadedData();
//            onFragmentVisible();
//        } else {
//            onFragmentInvisible();
//        }
//    }

    //由于使用的Fragmentation库已经做了懒加载处理，这里注释掉原有逻辑，转调原有方法即可

    @Override
    protected boolean isNeedLazy() {
        return true;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        onFragmentVisible();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        onFragmentInvisible();
    }

    @Override
    public void onFragmentVisible() {
    }

    @Override
    public void onFragmentInvisible() {
    }
}
