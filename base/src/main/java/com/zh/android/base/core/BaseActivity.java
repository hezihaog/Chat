package com.zh.android.base.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.List;

/**
 * <b>Project:</b> LingHitPlatform <br>
 * <b>Create Date:</b> 2018/2/21 <br>
 * <b>@author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> baseActivity <br>
 */
public abstract class BaseActivity extends BaseSupportActivity
        implements LayoutCallback, LifecycleOwnerExt {
    @Override
    public void onLayoutBefore() {
    }

    @Override
    public void setData() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        onLayoutBefore();
        int id = onInflaterViewId();
        if (id != -1) {
            setContentView(id);
        }
        View rootView = findViewById(android.R.id.content);
        onInflaterViewAfter(rootView);
        onBindView(rootView);
        setData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (!fragments.isEmpty()) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onInflaterViewAfter(View view) {
    }

    public LifecycleOwnerExt getLifecycleOwner() {
        return this;
    }

    @Override
    public FragmentActivity getHostActivity() {
        return this;
    }
}