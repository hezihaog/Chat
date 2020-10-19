package com.zh.android.circle.mall.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zh.android.circle.mall.R;
import com.zh.android.circle.mall.enums.OrderByType;

/**
 * @author wally
 * @date 2020/10/19
 * 商品排序分段控件
 */
public class GoodsSortSegment extends FrameLayout {
    private ViewGroup vRoot;

    /**
     * 选中的控件的Id
     */
    private int selectViewId;
    /**
     * 回调
     */
    private Callback callback;

    public GoodsSortSegment(@NonNull Context context) {
        this(context, null);
    }

    public GoodsSortSegment(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoodsSortSegment(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.mall_goods_sort_segment_view, this);
        findView(this);
        bindView();
    }

    private void findView(View view) {
        vRoot = view.findViewById(R.id.root);
    }

    private void bindView() {
        int childCount = vRoot.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = vRoot.getChildAt(i);
            if (childView instanceof TextView) {
                TextView view = (TextView) childView;
                view.setOnClickListener(mClickListener);
                //默认选中，第一个
                setSelect(view, i == 0);
            }
        }
    }

    /**
     * 点击监听
     */
    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //重复选中，不执行切换
            if (v.getId() == selectViewId) {
                return;
            }
            int childCount = vRoot.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = vRoot.getChildAt(i);
                if (childView instanceof TextView) {
                    if (childView == v) {
                        TextView selectView = (TextView) childView;
                        int viewId = selectView.getId();
                        //切换选中状态
                        setSelect(selectView, true);
                        //进行回调
                        OrderByType orderByType = null;
                        if (R.id.mall_recommend == viewId) {
                            orderByType = OrderByType.DEFAULT;
                        } else if (R.id.mall_new == viewId) {
                            orderByType = OrderByType.NEW;
                        } else if (R.id.mall_price == viewId) {
                            orderByType = OrderByType.PRICE;
                        }
                        if (orderByType == null) {
                            return;
                        }
                        if (callback != null) {
                            callback.onSelectChange(orderByType);
                        }
                    } else {
                        //切换为非选中状态
                        setSelect((TextView) childView, false);
                    }
                }
            }
        }
    };

    /**
     * 设置选中
     *
     * @param view     选中的分段按钮
     * @param isSelect 是否选中
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setSelect(TextView view, boolean isSelect) {
        Resources resources = getResources();
        if (isSelect) {
            selectViewId = view.getId();
            view.setTextColor(resources.getColor(R.color.base_white));
            view.setBackground(resources.getDrawable(R.drawable.mall_bg_segment_select));
        } else {
            view.setTextColor(resources.getColor(R.color.base_green3));
            view.setBackground(null);
        }
    }

    public interface Callback {
        void onSelectChange(OrderByType orderByType);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}