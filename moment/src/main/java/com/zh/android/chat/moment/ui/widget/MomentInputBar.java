package com.zh.android.chat.moment.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zh.android.base.util.SoftKeyBoardUtil;
import com.zh.android.base.util.listener.DelayOnClickListener;
import com.zh.android.chat.moment.R;

/**
 * @author wally
 * @date 2020/09/22
 * 动态输入栏
 */
public class MomentInputBar extends FrameLayout {
    private EditText vInput;
    private View vSendLayout;
    private View vLikeLayout;
    private ImageView vLikeSymbol;
    private TextView vLikeText;
    private View vCommentLayout;
    private TextView vCommentText;
    private OnActionCallback actionCallback;

    /**
     * 是否点赞
     */
    private boolean isLike;

    public MomentInputBar(@NonNull Context context) {
        this(context, null);
    }

    public MomentInputBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentInputBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.moment_input_bar, this);
        findView(this);
        bindView();
    }

    private void findView(View view) {
        vInput = view.findViewById(R.id.input);
        vSendLayout = view.findViewById(R.id.send_layout);
        vLikeLayout = view.findViewById(R.id.like_layout);
        vLikeSymbol = view.findViewById(R.id.like_symbol);
        vLikeText = view.findViewById(R.id.like_text);
        vCommentLayout = view.findViewById(R.id.comment_layout);
        vCommentText = view.findViewById(R.id.comment_text);
    }

    private void bindView() {
        vInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0) {
                    vSendLayout.setVisibility(View.GONE);
                } else {
                    vSendLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        vSendLayout.setOnClickListener(new DelayOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionCallback != null) {
                    String inputText = vInput.getText().toString().trim();
                    boolean isOk = actionCallback.onClickSendBefore(inputText);
                    if (isOk) {
                        actionCallback.onClickSendAfter(inputText);
                    }
                }
            }
        }));
        vLikeLayout.setOnClickListener(new DelayOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionCallback != null) {
                    actionCallback.onClickLike(!isLike);
                }
            }
        }));
        vCommentLayout.setOnClickListener(new DelayOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionCallback != null) {
                    actionCallback.onClickComment();
                }
            }
        }));
    }

    public interface OnActionCallback {
        /**
         * 点击发送时回调，用于判断文字内容
         *
         * @return 返回true，代表允许继续，false则不允许
         */
        boolean onClickSendBefore(String input);

        /**
         * 点击发送时回调
         */
        void onClickSendAfter(String inputText);

        /**
         * 点击点赞图标时回调
         *
         * @param isLike 是否点赞了
         */
        void onClickLike(boolean isLike);

        /**
         * 点击评论图标时回调
         */
        void onClickComment();
    }

    public static class OnActionCallbackAdapter implements OnActionCallback {
        @Override
        public boolean onClickSendBefore(String input) {
            //默认实现，只要文字不为空，就不拦截
            return !TextUtils.isEmpty(input);
        }

        @Override
        public void onClickSendAfter(String inputText) {
        }

        @Override
        public void onClickLike(boolean isLike) {
        }

        @Override
        public void onClickComment() {
        }
    }

    public void setOnActionCallback(OnActionCallback actionCallback) {
        this.actionCallback = actionCallback;
    }

    /**
     * 设置输入内容
     */
    public void setInputText(String inputText) {
        vInput.setText(inputText);
        vInput.setSelection(inputText.length());
        vInput.requestFocus();
    }

    /**
     * 设置输入框的提示文字
     */
    public void setInputHintText(String inputHintText) {
        vInput.setHint(inputHintText);
        vInput.requestFocus();
    }

    /**
     * 弹出软键盘
     */
    public void showKeyboardByInput() {
        SoftKeyBoardUtil.showKeyboard(vInput);
    }

    /**
     * 设置是否已点赞
     *
     * @param isLike  是否已点赞
     * @param likeNum 点赞数量
     */
    public void setLike(boolean isLike, String likeNum) {
        this.isLike = isLike;
        if (isLike) {
            vLikeSymbol.setImageResource(R.drawable.moment_liked);
            vLikeText.setTextColor(getContext().getResources().getColor(R.color.base_blue));
        } else {
            vLikeSymbol.setImageResource(R.drawable.moment_like);
            vLikeText.setTextColor(getContext().getResources().getColor(R.color.base_gray4));
        }
        vLikeText.setText(likeNum);
    }

    /**
     * 设置评论数量
     */
    public void setCommentNum(String commentNum) {
        vCommentText.setText(commentNum);
    }

    /**
     * 隐藏点赞按钮
     */
    public void hideLikeView() {
        vLikeLayout.setVisibility(View.GONE);
    }

    /**
     * 隐藏评论按钮
     */
    public void hideCommentView() {
        vCommentLayout.setVisibility(View.GONE);
    }
}