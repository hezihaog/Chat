package com.zh.android.chat.moment.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zh.android.base.util.rx.RxSchedulerUtil;
import com.zh.android.base.util.rx.RxUtil;
import com.zh.android.chat.moment.R;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author wally
 * @date 2020/10/14
 * 动态搜索栏
 */
public class MomentSearchBar extends FrameLayout {
    private EditText vInput;

    private Callback callback;
    private Disposable disposable;

    public MomentSearchBar(@NonNull Context context) {
        this(context, null);
    }

    public MomentSearchBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentSearchBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.moment_search_bar_view, this);
        findView(this);
        bindView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void findView(View view) {
        vInput = view.findViewById(R.id.input);
    }

    private void bindView() {
        //输入框
        disposable = RxUtil.textChangesWithDebounce(vInput)
                .compose(RxSchedulerUtil.ioToMain())
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        String keyword = charSequence.toString().trim();
                        if (TextUtils.isEmpty(keyword)) {
                            return;
                        }
                        if (callback != null) {
                            callback.onSearch(keyword);
                        }
                    }
                });
        //让输入框获取焦点
        vInput.setFocusable(true);
        vInput.setFocusableInTouchMode(true);
        vInput.requestFocus();
    }

    public interface Callback {
        void onSearch(String keyword);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setInputText(String inputText) {
        vInput.setText(inputText);
        vInput.setSelection(inputText.length());
    }

    public String getInputText() {
        return vInput.getText().toString().trim();
    }
}