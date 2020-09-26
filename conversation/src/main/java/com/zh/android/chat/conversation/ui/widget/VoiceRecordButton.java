package com.zh.android.chat.conversation.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.zh.android.chat.conversation.R;


/**
 * <b>Package:</b> com.hule.dashi.answer.widget <br>
 * <b>Create Date:</b> 2019/2/19  5:26 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 录制语音按钮 <br>
 */
public class VoiceRecordButton extends AppCompatTextView {
    /**
     * 最短录音时间
     **/
    private static final int MIN_INTERVAL_TIME = 1300;
    /**
     * 取消的滑动距离，默认为屏幕宽度的3分之一
     */
    public int mCancelDistance;
    /**
     * 按钮按下时间
     */
    long mDownTime = 0;
    private float mDownY = 0;
    private float mLastY = 0;

    private ButtonTouchCallback mCallback;

    public VoiceRecordButton(Context context) {
        super(context);
        init();
    }

    public VoiceRecordButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VoiceRecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCancelDistance = (int) (getScreenHeight(getContext()) / 3);
        setText(R.string.conversation_voice_normal);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        //只关心上下移动
        if (action == MotionEvent.ACTION_DOWN) {
            mDownY = event.getY();
            mLastY = event.getY();
            mDownTime = System.currentTimeMillis();
            setText(R.string.conversation_voice_normal);
            if (mCallback != null) {
                if (mCallback.isIntercept()) {
                    return super.onTouchEvent(event);
                }
                mCallback.onStart();
            }
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            float moveY = event.getY();
            float changeY = moveY - mLastY;
            if (moveY < 0 && Math.abs(changeY) >= mCancelDistance) {
                //上滑到了取消区域
                setText(R.string.conversation_voice_up_cancel);
                if (mCallback != null) {
                    mCallback.onTouchCancelArea();
                }
            } else {
                //不在取消区域范围内
                setText(R.string.conversation_voice_up_finish);
                if (mCallback != null) {
                    mCallback.onRestoreNormalTouchArea();
                }
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            float upY = event.getY();
            float distanceY = Math.abs(upY - mDownY);
            setText(R.string.conversation_voice_normal);
            long intervalTime = System.currentTimeMillis() - mDownTime;
            //不够最短时间
            if (intervalTime <= MIN_INTERVAL_TIME) {
                if (mCallback != null) {
                    mCallback.onTouchIntervalTimeSmall();
                }
            } else if (upY >= 0 || distanceY < mCancelDistance) {
                //在按钮以下或者还没到取消线，并且录制时间大于最小要求。录制成功
                if (mCallback != null) {
                    mCallback.onFinish();
                }
            } else if (upY < 0 && distanceY >= mCancelDistance) {
                //上滑了到取消区域
                if (mCallback != null) {
                    mCallback.onCancel();
                }
            }
            if (mCallback != null) {
                mCallback.onTerminate();
            }
            mLastY = event.getY();
        }
        return super.onTouchEvent(event);
    }

    public interface ButtonTouchCallback {
        /**
         * 是否拦截，如果返回true，代表拦截，则不进行触摸事件的监听
         *
         * @return true为拦截，false为不拦截
         */
        boolean isIntercept();

        /**
         * 开始录制
         */
        void onStart();

        /**
         * 手动取消录制
         */
        void onCancel();

        /**
         * 结束录制
         */
        void onFinish();

        /**
         * 准备退出，在手指抬起时回调
         */
        void onTerminate();

        /**
         * 间隔时间太小
         */
        void onTouchIntervalTimeSmall();

        /**
         * 当触碰到取消区域
         */
        void onTouchCancelArea();

        /**
         * 当恢复到正常区域
         */
        void onRestoreNormalTouchArea();
    }

    public void setButtonTouchCallback(ButtonTouchCallback callback) {
        this.mCallback = callback;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    public static int spToPixel(Context context, float spValue) {
        final float fontScale = getDisplayMetrics(context).scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static float getScreenHeight(Context context) {
        return (float) getDisplayMetrics(context).heightPixels;
    }
}