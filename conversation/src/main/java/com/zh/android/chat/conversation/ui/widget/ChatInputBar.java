package com.zh.android.chat.conversation.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zh.android.base.util.SoftKeyBoardUtil;
import com.zh.android.base.widget.CustomRadioButton;
import com.zh.android.base.widget.CustomRadioGroup;
import com.zh.android.chat.conversation.R;

/**
 * @author wally
 * @date 2020/09/26
 * 聊天输入框
 */
public class ChatInputBar extends FrameLayout {
    private View vMsgInputLayout;
    private EditText vMsgInput;
    private View vSend;
    private VoiceRecordButton vVoiceTouchBtn;
    private CustomRadioButton vVoiceRadioBtn;
    private CustomRadioGroup vChatRadioGroup;
    private ImageView vVoiceToggle;

    private Callback callback;

    public ChatInputBar(@NonNull Context context) {
        this(context, null);
    }

    public ChatInputBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatInputBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.conversation_chat_input_bar_view, this);
        findView(this);
        bindView();
    }

    private void findView(View view) {
        vMsgInputLayout = view.findViewById(R.id.msg_input_layout);
        vMsgInput = view.findViewById(R.id.msg_input);
        vSend = view.findViewById(R.id.send);
        vVoiceTouchBtn = findViewById(R.id.voice_touch_tv);
        vChatRadioGroup = findViewById(R.id.chat_radio_group);
        vVoiceRadioBtn = findViewById(R.id.voice_radio_btn);
        vVoiceToggle = findViewById(R.id.voice_toggle);
    }

    private void bindView() {
        vMsgInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    vSend.setVisibility(View.VISIBLE);
                } else {
                    vSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        vSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    String inputText = vMsgInput.getText().toString();
                    callback.onSend(inputText);
                }
            }
        });
        vChatRadioGroup.setOnDoubleCheckListener(new CustomRadioGroup.OnDoubleCheckListener() {
            @Override
            public boolean onDoubleCheck(CustomRadioGroup group, CustomRadioButton button) {
                //重选中时回调，返回true，需要反选自身，这样才能切换按钮
                return true;
            }

            @Override
            public void onDoubleCheckFinish(CustomRadioGroup group, CustomRadioButton button, boolean isChecked) {
//                if (isChecked) {
//                    //如果软键盘弹起，关掉软键盘
//                    if (isOpenSoftKeyBoard) {
//                        vMsgInput.clearFocus();
//                        SoftKeyBoardUtil.hideKeyboard(vMsgInput);
//                    }
//                } else {
//                    vMsgInput.requestFocus();
//                    SoftKeyBoardUtil.showKeyboard(vMsgInput);
//                }
            }
        });
        //语音按钮切换
        vVoiceRadioBtn.setOnCheckedStatusChangeListener(new CustomRadioButton.OnCheckedStatusChangeListener() {
            @Override
            public void onCheckedStatusChangeListener(CustomRadioButton button, boolean isChecked) {
                if (isChecked) {
                    //隐藏输入框，显示录音框
                    vVoiceTouchBtn.setVisibility(View.VISIBLE);
                    vMsgInputLayout.setVisibility(View.GONE);
                    //隐藏软键盘
                    SoftKeyBoardUtil.hideKeyboard(vMsgInput);
                    //显示软键盘图标
                    vVoiceToggle.setImageResource(R.drawable.conversation_toggle_keyboard);
                } else {
                    //显示输入框，隐藏录音框
                    vVoiceTouchBtn.setVisibility(View.GONE);
                    vMsgInputLayout.setVisibility(View.VISIBLE);
                    //显示录音图标
                    vVoiceToggle.setImageResource(R.drawable.conversation_toggle_voice);
                }
            }
        });
    }

    public interface Callback {
        /**
         * 点击发送时回调
         */
        void onSend(String inputText);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * 设置输入的文字
     */
    public void setInputText(String inputText) {
        vMsgInput.setText(inputText);
        vMsgInput.setSelection(inputText.length());
    }

    public void setVoiceButtonTouchCallback(VoiceRecordButton.ButtonTouchCallback voiceButtonCallback) {
        this.vVoiceTouchBtn.setButtonTouchCallback(voiceButtonCallback);
    }
}