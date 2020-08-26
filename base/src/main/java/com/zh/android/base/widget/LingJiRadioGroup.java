package com.zh.android.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;


import com.zh.android.base.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Package:</b> com.hule.dashi.answer.widget <br>
 * <b>Create Date:</b> 2019/1/16  10:03 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 自定义单选组 <br>
 */
public class LingJiRadioGroup extends LinearLayout {
    /**
     * 当前选择的按钮
     */
    private LingJiRadioButton mCurrentCheckButton;
    /**
     * 上次选择的按钮
     */
    private LingJiRadioButton mPreCheckButton;
    private OnCheckedChangeListener mCheckedChangeListener;
    private OnPrepareCheckListener mPrepareCheckListener;
    private OnDoubleCheckListener mDoubleCheckListener;
    /**
     * 是否默认选中第一个，默认都不选中
     */
    private boolean mIsDefaultCheckOneBtn = false;

    public LingJiRadioGroup(Context context) {
        super(context);
        init(context, null, 0);
    }

    public LingJiRadioGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public LingJiRadioGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(R.styleable.LingJiRadioGroup);
            mIsDefaultCheckOneBtn = typedArray.getBoolean(R.styleable.LingJiRadioGroup_crg_is_default_checked_one, false);
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //是否存在有按钮设置了选中
        boolean isHasCheckedBtn = false;
        //获取所有子按钮
        List<LingJiRadioButton> allButtonList = getAllButton();
        for (LingJiRadioButton button : allButtonList) {
            //配置默认选择
            if (button.isChecked()) {
                if (!isHasCheckedBtn) {
                    //只保存一次，只要有按钮是设置选中了，就是存在选中的按钮
                    isHasCheckedBtn = true;
                }
                setCheckButton(button);
            } else {
                button.setChecked(false);
            }
            button.setOnClickChatRadioButtonListener(new LingJiRadioButton.OnClickChatRadioButtonListener() {
                @Override
                public void onClickCustomRadioButton(LingJiRadioButton button) {
                    if (button.isChecked()) {
                        //如果之前就已经选择了，回调二次选择
                        if (mDoubleCheckListener != null) {
                            boolean isNeedUncheck = mDoubleCheckListener.onDoubleCheck(LingJiRadioGroup.this, button);
                            if (isNeedUncheck) {
                                button.setChecked(false);
                            }
                            mDoubleCheckListener.onDoubleCheckFinish(LingJiRadioGroup.this, button, button.isChecked());
                        }
                    } else {
                        boolean isIntercept = false;
                        //切换当前按钮，回调
                        if (mPrepareCheckListener != null) {
                            isIntercept = mPrepareCheckListener.onPrepareCheck(LingJiRadioGroup.this, button);
                        }
                        //不拦截，则进行选中
                        if (!isIntercept) {
                            setCheckButton(button);
                        }
                    }
                }
            });
        }
        //是否是自动选择第一个
        if (mIsDefaultCheckOneBtn) {
            //如果没有配置默认选择，默认选择第一个
            if (!isHasCheckedBtn) {
                setCheckButton(getAllButton().get(0));
            }
        }
    }

    /**
     * 获取所有按钮
     */
    public List<LingJiRadioButton> getAllButton() {
        List<LingJiRadioButton> buttons = new ArrayList<>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i) instanceof LingJiRadioButton) {
                buttons.add((LingJiRadioButton) getChildAt(i));
            }
        }
        return buttons;
    }

    /**
     * 使用按钮的Id，设置当前选择的按钮
     */
    public void setCheckButton(int buttonId) {
        LingJiRadioButton needCheckButton = null;
        List<LingJiRadioButton> allButton = getAllButton();
        for (LingJiRadioButton button : allButton) {
            if (buttonId == button.getId()) {
                needCheckButton = button;
                break;
            }
        }
        if (needCheckButton != null) {
            setCheckButton(needCheckButton);
        }
    }

    /**
     * 设置当前选择的按钮
     */
    public void setCheckButton(LingJiRadioButton checkButton) {
        //将当前需要选择的选中
        checkButton.setChecked(true);
        //将不是当前的取消选中
        List<LingJiRadioButton> allButtonList = getAllButton();
        for (int i = 0; i < allButtonList.size(); i++) {
            LingJiRadioButton needUncheckButton = allButtonList.get(i);
            if (needUncheckButton != checkButton) {
                needUncheckButton.setChecked(false);
            }
        }
        //更新配置
        this.mPreCheckButton = mCurrentCheckButton;
        this.mCurrentCheckButton = checkButton;
        if (mCheckedChangeListener != null) {
            mCheckedChangeListener.onCheckChange(this, mCurrentCheckButton, mPreCheckButton);
        }
    }

    /**
     * 取消选中所有按钮
     */
    public void uncheckAllButton() {
        List<LingJiRadioButton> allButtonList = getAllButton();
        for (LingJiRadioButton button : allButtonList) {
            button.setChecked(false);
            this.mPreCheckButton = mCurrentCheckButton;
            this.mCurrentCheckButton = button;
            if (mCheckedChangeListener != null) {
                mCheckedChangeListener.onCheckChange(this, mCurrentCheckButton, mPreCheckButton);
            }
        }
    }

    /**
     * 获取当前选择的按钮
     */
    public LingJiRadioButton getCheckButton() {
        return this.mCurrentCheckButton;
    }

    /**
     * 获取上一次选择的按钮
     */
    public LingJiRadioButton getPreCheckButton() {
        return this.mPreCheckButton;
    }

    /**
     * 单选改变的监听
     */
    public interface OnCheckedChangeListener {
        /**
         * 当选择改变时回调
         *
         * @param group         按钮所在组
         * @param checkedButton 当前选择的按钮
         * @param uncheckButton 被反选的按钮
         */
        void onCheckChange(LingJiRadioGroup group, LingJiRadioButton checkedButton, LingJiRadioButton uncheckButton);
    }

    /**
     * 准备选中的监听
     */
    public interface OnPrepareCheckListener {
        /**
         * 准备选择时会回调
         *
         * @param group              按钮的组
         * @param prepareCheckButton 准备选择的按钮
         * @return 是否拦截，返回true代表拦截，则允许选择，返回false代表允许选择
         */
        boolean onPrepareCheck(LingJiRadioGroup group, LingJiRadioButton prepareCheckButton);
    }

    /**
     * 当两次选择时回调，例如首页Table选择了，再点一次，进行回顶刷新这样的操作
     */
    public interface OnDoubleCheckListener {
        /**
         * 两次选择时回调，有返回值，决定是否需要反选
         *
         * @param group  按钮的组
         * @param button 按钮
         * @return 返回true为需要反选，返回false为不需要
         */
        boolean onDoubleCheck(LingJiRadioGroup group, LingJiRadioButton button);

        /**
         * 重选中结束后的回调，在这里可以处理重选中而取消选中时的处理
         *
         * @param isChecked 是否选中
         */
        void onDoubleCheckFinish(LingJiRadioGroup group, LingJiRadioButton button, boolean isChecked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener checkedChangeListener) {
        this.mCheckedChangeListener = checkedChangeListener;
    }

    public void setOnPrepareCheckListener(OnPrepareCheckListener prepareCheckListener) {
        this.mPrepareCheckListener = prepareCheckListener;
    }

    public void setOnDoubleCheckListener(OnDoubleCheckListener doubleCheckListener) {
        this.mDoubleCheckListener = doubleCheckListener;
    }
}