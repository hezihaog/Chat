package com.zh.android.base.widget;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.Nullable;

import com.zh.android.base.R;

/**
 * 仿微信切换按钮
 *
 * @author wally
 */
public class SwitchButton extends View {
    /**
     * 背景动画开始前的延时时间
     */
    private static final int BG_ANIMATOR_START_DELAY = 0;
    /**
     * 背景动画执行时间
     */
    private static final int BG_ANIMATOR_DURATION = 200;

    /**
     * 选中的背景颜色
     */
    private int mCheckedBg;
    /**
     * 未选中的背景颜色
     */
    private int mUncheckedBg;
    /**
     * 当前边界背景
     */
    private int mCurrentBoundBg;
    /**
     * 小圆与背景的间距
     */
    private int mSmallCircleMargin;
    /**
     * 背景画笔
     */
    private Paint mBgPaint;
    /**
     * 小圆画笔
     */
    private Paint mSmallCirclePaint;
    /**
     * 默认的宽度
     */
    private int mDefaultWidth;
    /**
     * 默认高度
     */
    private int mDefaultHeight;
    /**
     * 小圆半径
     */
    private int mSmallCircleRadius;
    /**
     * View的宽
     */
    private int mWidth;
    /**
     * View的高
     */
    private int mHeight;
    /**
     * 边界的圆的半径
     */
    private float mBoundRadius;
    /**
     * 边界区域
     */
    private RectF mBoundRect;
    /**
     * 小圆中心位置的X坐标，开和关会改变该值来移动小圆
     */
    private int mSmallCircleCenterX;
    /**
     * 小圆中心位置的Y坐标，一直在高度的一半
     */
    private int mSmallCircleCenterY;
    /**
     * 是否打开
     */
    private boolean isChecked;
    /**
     * 状态监听
     */
    private OnCheckedChangeListener mCheckedChangeListener;
    /**
     * 小圆的起点X和终点x坐标
     */
    private int mSmallCircleStartX;
    private int mSmallCircleEndX;
    /**
     * 背景颜色估值器，用来做2种背景颜色过渡切换时，估算过渡颜色值
     */
    final ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();
    /**
     * 开关的开、关动画
     */
    private AnimatorSet mOffAnimatorSet;
    private AnimatorSet mOpenAnimatorSet;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton, defStyleAttr, defStyleRes);
        //开关-开时，背景颜色
        this.mCheckedBg = typedArray.getColor(R.styleable.SwitchButton_sb_checked_bg, Color.BLUE);
        //开关-关时，背景颜色
        this.mUncheckedBg = typedArray.getColor(R.styleable.SwitchButton_sb_unchecked_bg, Color.GRAY);
        //是否打开
        this.isChecked = typedArray.getBoolean(R.styleable.SwitchButton_sb_checked, false);
        //小圆距离背景边缘时的距离
        this.mSmallCircleMargin = (int) typedArray.getDimension(R.styleable.SwitchButton_sb_circle_bg_margin, dip2px(getContext(), 3f));
        typedArray.recycle();
        //按状态切换按钮背景颜色
        if (isChecked) {
            this.mCurrentBoundBg = mCheckedBg;
        } else {
            this.mCurrentBoundBg = mUncheckedBg;
        }
        //定义默认宽度和高度
        this.mDefaultWidth = dip2px(getContext(), 50f);
        this.mDefaultHeight = dip2px(getContext(), 30f);
        init();
    }

    private void init() {
        //内容画笔
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStyle(Paint.Style.FILL);
        //小圆画笔
        mSmallCirclePaint = new Paint();
        mSmallCirclePaint.setAntiAlias(true);
        mSmallCirclePaint.setStyle(Paint.Style.FILL);
        mSmallCirclePaint.setColor(getResources().getColor(android.R.color.white));
        //设置点击监听来进行开关切换
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked) {
                    //已打开，则关闭
                    if (mOffAnimatorSet != null && mOffAnimatorSet.isRunning()) {
                        return;
                    }
                    off();
                } else {
                    //已关闭，则打开
                    if (mOpenAnimatorSet != null && mOpenAnimatorSet.isRunning()) {
                        return;
                    }
                    open();
                }
                isChecked = !isChecked;
                if (mCheckedChangeListener != null) {
                    mCheckedChangeListener.onCheckedChanged(SwitchButton.this, isChecked);
                }
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        //定义小圆的半径
        this.mSmallCircleRadius = (mHeight - (mSmallCircleMargin * 2)) / 2;
        //定义背景圆弧的半径
        this.mBoundRadius = mHeight / 2;
        //定义小圆的中间点Y
        this.mSmallCircleCenterY = mHeight / 2;
        //定义小圆的起点X，为宽度的4分之一
        this.mSmallCircleStartX = mWidth / 4;
        //定义小圆的终点x坐标，为宽度4分之一的3倍
        this.mSmallCircleEndX = (mWidth / 4) * 3;
        //因为一开始外部调用setChecked()会比onSizeChanged方法快，所以回显状态时，马上就得改变当前状态的相关配置
        updateCheckStatus();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int resultWidth;
        int resultHeight;
        //处理wrap_content时的，默认宽、高
        if (widthMode == MeasureSpec.EXACTLY
                || heightMode == MeasureSpec.EXACTLY) {
            resultWidth = widthSize;
            resultHeight = heightSize;
        } else {
            resultWidth = mDefaultWidth;
            resultHeight = mDefaultHeight;
        }
        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画内容
        drawBg(canvas, mBgPaint);
        //画小圆
        drawCircle(canvas, mSmallCirclePaint);
    }

    /**
     * 画背景
     */
    private void drawBg(Canvas canvas, Paint paint) {
        mBgPaint.setColor(mCurrentBoundBg);
        //画边界背景
        if (mBoundRect == null) {
            mBoundRect = new RectF(0, 0, mWidth, mHeight);
        }
        canvas.drawRoundRect(mBoundRect, mBoundRadius, mBoundRadius, paint);
    }

    /**
     * 画小圆
     */
    private void drawCircle(Canvas canvas, Paint paint) {
        //画小圆
        canvas.drawCircle(mSmallCircleCenterX, mSmallCircleCenterY, mSmallCircleRadius, paint);
    }

    /**
     * 开启按钮
     */
    private void open() {
        //从左到右
        final int startX = mSmallCircleStartX;
        final int endX = mSmallCircleEndX;
        mOpenAnimatorSet = new AnimatorSet();
        ValueAnimator openAnimator = ValueAnimator.ofInt(startX, endX);
        openAnimator.setDuration(300);
        openAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSmallCircleCenterX = (Integer) animation.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator bgAnimator = ValueAnimator.ofFloat(0, 1f);
        //开关切换完后，再切换背景
        bgAnimator.setStartDelay(BG_ANIMATOR_START_DELAY);
        bgAnimator.setDuration(BG_ANIMATOR_DURATION);
        bgAnimator.setInterpolator(new AccelerateInterpolator());
        bgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float fraction = (Float) animation.getAnimatedValue();
                mCurrentBoundBg = (int) mArgbEvaluator.evaluate(fraction, mUncheckedBg, mCheckedBg);
                invalidate();
            }
        });
        mOpenAnimatorSet.playTogether(openAnimator, bgAnimator);
        mOpenAnimatorSet.start();
    }

    /**
     * 关闭按钮
     */
    private void off() {
        //从右到左
        final int startX = mSmallCircleEndX;
        final int endX = mSmallCircleStartX;
        mOffAnimatorSet = new AnimatorSet();
        ValueAnimator offAnimator = ValueAnimator.ofInt(startX, endX);
        offAnimator.setDuration(200);
        offAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //小圆位置
                mSmallCircleCenterX = (Integer) animation.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator bgAnimator = ValueAnimator.ofFloat(0, 1f);
        bgAnimator.setStartDelay(BG_ANIMATOR_START_DELAY);
        bgAnimator.setDuration(BG_ANIMATOR_DURATION);
        bgAnimator.setInterpolator(new AccelerateInterpolator());
        bgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float fraction = (Float) animation.getAnimatedValue();
                mCurrentBoundBg = (int) mArgbEvaluator.evaluate(fraction, mCheckedBg, mUncheckedBg);
                invalidate();
            }
        });
        mOffAnimatorSet.playTogether(offAnimator, bgAnimator);
        mOffAnimatorSet.start();
    }

    /**
     * 设置按钮开、关
     */
    public void setChecked(final boolean checked) {
        this.isChecked = checked;
        updateCheckStatus();
        postInvalidate();
    }

    /**
     * 更新开关状态
     */
    private void updateCheckStatus() {
        if (isChecked) {
            this.mSmallCircleCenterX = mSmallCircleEndX;
            this.mCurrentBoundBg = mCheckedBg;
        } else {
            this.mSmallCircleCenterX = mSmallCircleStartX;
            this.mCurrentBoundBg = mUncheckedBg;
        }
    }

    /**
     * 是否打开
     */
    public boolean isChecked() {
        return isChecked;
    }

    public interface OnCheckedChangeListener {
        /**
         * 按钮开关改变
         *
         * @param button    按钮
         * @param isChecked 是否打开
         */
        void onCheckedChanged(SwitchButton button, boolean isChecked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener checkedChangeListener) {
        this.mCheckedChangeListener = checkedChangeListener;
    }

    public OnCheckedChangeListener getCheckedChangeListener() {
        return mCheckedChangeListener;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }
}