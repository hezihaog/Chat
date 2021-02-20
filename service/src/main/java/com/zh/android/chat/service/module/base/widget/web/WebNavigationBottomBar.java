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

import com.zh.android.base.util.listener.DelayOnClickListener;
import com.zh.android.chat.service.R;

/**
 * @author wally
 * @date 2021/01/28
 * 底部导航栏
 */
public class WebNavigationBottomBar extends FrameLayout {
    private TextView vGoBack;
    private TextView vForward;
    private TextView vNewUrl;
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
        LayoutInflater.from(context).inflate(R.layout.service_web_navigation_bar_view, this);
        findView(this);
        bindView();
    }

    private void findView(View view) {
        vGoBack = view.findViewById(R.id.go_back);
        vForward = view.findViewById(R.id.forward);
        vNewUrl = view.findViewById(R.id.new_url);
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
        vNewUrl.setOnClickListener(new DelayOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    mCallBack.onNewUrl();
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
        vCollect.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mCallBack != null) {
                    return mCallBack.onGoCollectList();
                }
                return false;
            }
        });
        render();
    }

    /**
     * 渲染
     */
    private void render() {
        Resources resources = getContext().getResources();
        int enableColor = resources.getColor(R.color.base_black);
        int disableColor = resources.getColor(R.color.base_gray4);
        if (isCanGoBack) {
            vGoBack.setTextColor(enableColor);
        } else {
            vGoBack.setTextColor(disableColor);
        }
        if (isCanForward) {
            vForward.setTextColor(enableColor);
        } else {
            vForward.setTextColor(disableColor);
        }
        if (isCollect) {
            vCollect.setText(resources.getString(R.string.service_web_navigation_bar_is_collect));
            enableColor = resources.getColor(R.color.base_red);
            vCollect.setTextColor(enableColor);
        } else {
            vCollect.setText(resources.getString(R.string.service_web_navigation_bar_not_collect));
            disableColor = resources.getColor(R.color.base_black);
            vCollect.setTextColor(disableColor);
        }
    }

    public interface CallBack {
        void onGoBack();

        void onForward();

        /**
         * 打开新地址
         */
        void onNewUrl();

        void onRefresh();

        /**
         * 切换收藏
         *
         * @param isCollect 当前是否收藏
         */
        void onCollect(boolean isCollect);

        /**
         * 跳转到收藏列表
         */
        boolean onGoCollectList();
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