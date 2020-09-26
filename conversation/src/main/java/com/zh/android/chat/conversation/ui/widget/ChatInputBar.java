package com.zh.android.chat.conversation.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zh.android.chat.conversation.R;

/**
 * @author wally
 * @date 2020/09/26
 * 聊天输入框
 */
public class ChatInputBar extends FrameLayout {
    private EditText vMsgInput;
    private View vSend;
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
        vMsgInput = view.findViewById(R.id.msg_input);
        vSend = view.findViewById(R.id.send);
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
}