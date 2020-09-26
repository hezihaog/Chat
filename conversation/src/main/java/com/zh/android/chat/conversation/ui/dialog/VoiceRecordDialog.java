package com.zh.android.chat.conversation.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zh.android.chat.conversation.R;


/**
 * <b>Package:</b> com.hule.dashi.answer.consult.dialog <br>
 * <b>Create Date:</b> 2019/2/19  6:39 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 语音录制弹窗 <br>
 */
public class VoiceRecordDialog extends Dialog {
    private ImageView vStateImage;
    private TextView vStateText;

    /**
     * 是否当前在正常区域，该标志位是为了避免一直在正常区域不断回调，导致弹窗不能显示的问题
     */
    private boolean isTouchInNormalArea = false;

    public VoiceRecordDialog(@NonNull Context context) {
        super(context, R.style.answer_ask_voice_dialog_style);
        init();
    }

    private void init() {
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.answer_ask_voice_record_dialog_layout, null);
        findView(layout);
        bindView();
        setContentView(layout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        //去掉白色的背景
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void findView(View view) {
        vStateImage = view.findViewById(R.id.state_image);
        vStateText = view.findViewById(R.id.state_text);
    }

    private void bindView() {
        vStateImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.answer_ask_voice_anim_decibel));
        vStateText.setText(R.string.answer_ask_voice_dialog_up_cancel);
    }

    /**
     * 通知-录制异常
     */
    public void notifyRecordError() {
        updateStateTipText(R.string.answer_ask_voice_dialog_error, false);
        updateStateImage(R.drawable.answer_ask_voice_wraning);
        if (!isShowing()) {
            show();
        }
    }

    /**
     * 通知开始录制
     */
    public void notifyStartRecord() {
        updateStateTipText(R.string.answer_ask_voice_dialog_up_cancel, false);
        Drawable stateImageDrawable = vStateImage.getDrawable();
        if (stateImageDrawable instanceof AnimationDrawable) {
            //录制动画
            AnimationDrawable voiceAnimation = (AnimationDrawable) vStateImage.getDrawable();
            startRecordAnimation(voiceAnimation);
        } else {
            AnimationDrawable drawable = (AnimationDrawable) getContext().getResources().getDrawable(R.drawable.answer_ask_voice_anim_decibel);
            vStateImage.setImageDrawable(drawable);
            startRecordAnimation(drawable);
        }
        if (!isShowing()) {
            show();
        }
    }

    /**
     * 通知-结束录制
     */
    public void notifyFinishRecord() {
        stopVoiceAnimation();
        if (isShowing()) {
            dismiss();
        }
    }

    /**
     * 通知-取消录制
     */
    public void notifyCancelRecord() {
        if (isShowing()) {
            dismiss();
        }
    }

    /**
     * 通知-录制时间太短
     */
    public void notifyTouchIntervalTimeSmall() {
        updateStateTipText(R.string.answer_ask_voice_dialog_interval_time_small, false);
        updateStateImage(R.drawable.answer_ask_voice_wraning);
        if (!isShowing()) {
            show();
        }
    }

    /**
     * 通知-手指移动到取消区域
     */
    public void notifyTouchCancelArea() {
        if (!isTouchInNormalArea) {
            return;
        }
        isTouchInNormalArea = false;
        updateStateTipText(R.string.answer_ask_voice_dialog_up_finish, true);
        updateStateImage(R.drawable.answer_ask_voice_cancel);
        if (!isShowing()) {
            show();
        }
    }

    /**
     * 通知-手指移动到正常区域
     */
    public void notifyRestoreNormalTouchArea() {
        if (isTouchInNormalArea) {
            return;
        }
        isTouchInNormalArea = true;
        notifyStartRecord();
    }

    /**
     * 播放录制动画
     */
    private void startRecordAnimation(AnimationDrawable voiceAnimation) {
        if (!voiceAnimation.isRunning()) {
            voiceAnimation.start();
        }
    }

    /**
     * 停止录音动画
     */
    private void stopVoiceAnimation() {
        Drawable drawable = vStateImage.getDrawable();
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable voiceAnimation = (AnimationDrawable) drawable;
            voiceAnimation.stop();
        }
    }

    /**
     * 更新状态图
     */
    public void updateStateImage(int imageResId) {
        vStateImage.setImageResource(imageResId);
    }

    /**
     * 更新状态文字
     *
     * @param tipTextResId  提示文字的资源Id
     * @param isHighlightBg 是否高亮背景
     */
    private void updateStateTipText(int tipTextResId, boolean isHighlightBg) {
        vStateText.setText(tipTextResId);
        if (isHighlightBg) {
            vStateText.setBackgroundResource(R.drawable.answer_ask_bg_voice_cancel);
        } else {
            vStateText.setBackground(null);
        }
    }

    /**
     * 隐藏弹窗
     */
    public void dismissDialog() {
        if (isShowing()) {
            dismiss();
        }
    }
}