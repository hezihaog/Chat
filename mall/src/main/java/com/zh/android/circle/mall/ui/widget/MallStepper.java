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
 * @date 2020/10/20
 * 计数器、步进器
 */
public class MallStepper extends FrameLayout {
    private TextView vDecrement;
    private TextView vValue;
    private TextView vIncrease;

    /**
     * 步长，默认为1
     */
    private int stepValue = 1;
    /**
     * 最小值，默认为1
     */
    private int minimumValue = 1;
    /**
     * 最大值，默认为100
     */
    private int maximumValue = 100;
    /**
     * 回调
     */
    private Callback callback;

    public MallStepper(@NonNull Context context) {
        this(context, null);
    }

    public MallStepper(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MallStepper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.mall_stepper_view, this);
        findView(this);
        bindView();
    }

    private void findView(View view) {
        vDecrement = view.findViewById(R.id.decrement);
        vValue = view.findViewById(R.id.value);
        vIncrease = view.findViewById(R.id.increase);
    }

    private void bindView() {
        //减号
        vDecrement.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    int currentValue = getCurrentValue();
                    int nextValue = currentValue - stepValue;
                    //如果比最小值还小，则不能操作
                    if (nextValue < minimumValue) {
                        return;
                    }
                    //设置新值
                    setCurrentValue(nextValue);
                    //回调
                    if (callback != null) {
                        callback.onClickDecrement(
                                currentValue,
                                nextValue
                        );
                    }
                }
            }
        });
        //加号
        vIncrease.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    int currentValue = getCurrentValue();
                    int nextValue = currentValue + stepValue;
                    //如果比最大值还大，则不能操作
                    if (nextValue > maximumValue) {
                        return;
                    }
                    //设置新值
                    setCurrentValue(nextValue);
                    //回调
                    if (callback != null) {
                        callback.onClickIncrement(
                                currentValue,
                                nextValue
                        );
                    }
                }
            }
        });
    }

    /**
     * 设置步长
     */
    public void setStepValue(int stepValue) {
        this.stepValue = stepValue;
    }

    /**
     * 设置最小值
     */
    public void setMinimumValue(int minimumValue) {
        this.minimumValue = minimumValue;
    }

    /**
     * 设置最大值
     */
    public void setMaximumValue(int maximumValue) {
        this.maximumValue = maximumValue;
    }

    /**
     * 设置当前值
     *
     * @param newValue 新值
     */
    public void setCurrentValue(int newValue) {
        if (newValue < minimumValue) {
            throw new RuntimeException("不能比最小值还小");
        }
        if (newValue > maximumValue) {
            throw new RuntimeException("不能比最大值还大");
        }
        //设置新值
        vValue.setText(String.valueOf(newValue));
    }

    /**
     * 获取当前值
     */
    public int getCurrentValue() {
        String value = vValue.getText().toString();
        if (TextUtils.isEmpty(value)) {
            value = "0";
        }
        return Integer.parseInt(value);
    }

    public interface Callback {
        /**
         * 点击了减号时回调
         *
         * @param currentValue 当前的值
         * @param newValue     准备更新的值
         */
        void onClickDecrement(int currentValue, int newValue);

        /**
         * 点击了加号时回调
         *
         * @param currentValue 当前的值
         * @param newValue     准备更新的值
         */
        void onClickIncrement(int currentValue, int newValue);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}