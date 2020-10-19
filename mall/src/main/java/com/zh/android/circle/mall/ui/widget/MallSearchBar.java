package com.zh.android.circle.mall.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zh.android.circle.mall.R;

/**
 * @author wally
 * @date 2020/10/19
 * 商城搜索栏
 */
public class MallSearchBar extends FrameLayout {
    private View vRoot;
    private TextView vTip;

    private Callback callback;
    private String tip;

    public MallSearchBar(@NonNull Context context) {
        this(context, null);
    }

    public MallSearchBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MallSearchBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.mall_search_bar_view, this);
        findView(this);
        bindView();
    }

    private void findView(View view) {
        vRoot = view.findViewById(R.id.root);
        vTip = view.findViewById(R.id.tip);
    }

    private void bindView() {
        vRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onClickSearBar();
                }
            }
        });
    }

    private void renderTip() {
        if (TextUtils.isEmpty(tip)) {
            vTip.setVisibility(View.INVISIBLE);
        } else {
            vTip.setVisibility(View.VISIBLE);
            vTip.setText(tip);
        }
    }

    public interface Callback {
        void onClickSearBar();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setTip(String tip) {
        this.tip = tip;
        renderTip();
    }
}