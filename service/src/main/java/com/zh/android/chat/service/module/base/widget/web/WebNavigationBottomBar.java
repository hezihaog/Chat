package com.zh.android.chat.service.module.base.widget.web;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zh.android.base.R;
import com.zh.android.base.util.listener.DelayOnClickListener;

/**
 * @author wally
 * @date 2021/01/28
 * 底部导航栏
 */
public class WebNavigationBottomBar extends FrameLayout {
    private TextView vGoBack;
    private TextView vForward;
    private TextView vRefresh;
    private TextView vCollect;

    private CallBack mCallBack;

    /**
     * 是否可以返回
     */
    private boolean isCanGoBack;
    /**
     * 是否可以前进
     */
    private boolean isCanForward;
    /**
     * 是否收藏
     */
    private boolean isCollect;

    public WebNavigationBottomBar(@NonNull Context context) {
        this(context, null);
    }

    public WebNavigationBottomBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebNavigationBottomBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.base_web_navigation_bar_view, this);
        findView(this);
        bindView();
    }

    private void findView(View view) {
        vGoBack = view.findViewById(R.id.go_back);
        vForward = view.findViewById(R.id.forward);
        vRefresh = view.findViewById(R.id.refresh);
        vCollect = view.findViewById(R.id.collect);
    }

    private void bindView() {
        vGoBack.setOnClickListener(new DelayOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    mCallBack.onGoBack();
                }
            }
        }));
        vForward.setOnClickListener(new DelayOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    mCallBack.onForward();
                }
            }
        }));
        vRefresh.setOnClickListener(new DelayOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    mCallBack.onRefresh();
                }
            }
        }));
        vCollect.setOnClickListener(new DelayOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    mCallBack.onCollect(isCollect);
                }
            }
        }));
        render();
    }

    /**
     * 渲染
     */
    private void render() {
        Resources resources = getContext().getResources();
        if (isCanGoBack) {
            vGoBack.setTextColor(resources.getColor(R.color.base_black));
        } else {
            vGoBack.setTextColor(resources.getColor(R.color.base_gray));
        }
        if (isCanForward) {
            vForward.setTextColor(resources.getColor(R.color.base_black));
        } else {
            vForward.setTextColor(resources.getColor(R.color.base_gray));
        }
        if (isCollect) {
            vCollect.setText(resources.getString(R.string.base_web_navigation_bar_is_collect));
            vCollect.setTextColor(resources.getColor(R.color.base_red));
        } else {
            vCollect.setText(resources.getString(R.string.base_web_navigation_bar_not_collect));
            vCollect.setTextColor(resources.getColor(R.color.base_black));
        }
    }

    public interface CallBack {
        void onGoBack();

        void onForward();

        void onRefresh();

        /**
         * 切换收藏
         *
         * @param isCollect 当前是否收藏
         */
        void onCollect(boolean isCollect);
    }

    /**
     * 设置回调
     */
    public void setCallBack(CallBack callBack) {
        this.mCallBack = callBack;
    }

    /**
     * 设置是否可以返回
     */
    public void setCanGoBack(boolean isCanGoBack) {
        this.isCanGoBack = isCanGoBack;
        render();
    }

    /**
     * 设置是否可以前进
     */
    public void setCanForward(boolean isCanForward) {
        this.isCanForward = isCanForward;
        render();
    }

    /**
     * 设置是否收藏
     *
     * @param collect 是否收藏
     */
    public void setCollect(boolean collect) {
        this.isCollect = collect;
        render();
    }

    public boolean isCollect() {
        return isCollect;
    }
}