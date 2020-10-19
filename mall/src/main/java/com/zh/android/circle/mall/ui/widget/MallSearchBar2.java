package com.zh.android.circle.mall.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zh.android.base.util.rx.RxSchedulerUtil;
import com.zh.android.base.util.rx.RxUtil;
import com.zh.android.circle.mall.R;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author wally
 * @date 2020/10/19
 * 商品搜索页的搜索栏
 */
public class MallSearchBar2 extends FrameLayout {
    private EditText vInput;

    private Disposable disposable;
    private Callback callback;

    public MallSearchBar2(@NonNull Context context) {
        this(context, null);
    }

    public MallSearchBar2(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MallSearchBar2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.mall_search_bar2_view, this);
        findView(this);
        bindView();
    }

    private void findView(View view) {
        vInput = view.findViewById(R.id.input);
    }

    private void bindView() {
        disposable = RxUtil.textChangesWithDebounce(vInput)
                .compose(RxSchedulerUtil.ioToMain())
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence inputText) throws Exception {
                        String keyword = inputText.toString().trim();
                        if (TextUtils.isEmpty(keyword)) {
                            return;
                        }
                        if (callback != null) {
                            callback.onSearch(keyword);
                        }
                    }
                });
    }

    public interface Callback {
        void onSearch(String keyword);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setInputText(String text) {
        vInput.setText(text);
        vInput.setSelection(text.length());
    }

    public String getInputText() {
        return vInput.getText().toString().trim();
    }
}