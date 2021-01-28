package com.zh.android.base.widget.iconfont;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.zh.android.base.R;

/**
 * @author wally
 * @date 2020/08/26
 * 支持IconFont的TextView
 */
public class IconFontTextView extends AppCompatTextView {
    /**
     * App图标
     */
    private static final int TYPEFACE_CODE_APP = 1;
    /**
     * Flutter图标
     */
    private static final int TYPEFACE_CODE_FLUTTER = 2;

    private Typeface ttfTypeface = null;

    public IconFontTextView(Context context) {
        this(context, null);
    }

    public IconFontTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconFontTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IconFontTextView, defStyleAttr, defStyleRes);
        int typefaceCode = typedArray.getInt(R.styleable.IconFontTextView_ift_typeface, TYPEFACE_CODE_APP);
        //加载字体文件
        Typeface typeface;
        if (typefaceCode == TYPEFACE_CODE_APP) {
            typeface = getTypeface(context, "fonts/icon_font.ttf");
        } else if (typefaceCode == TYPEFACE_CODE_FLUTTER) {
            typeface = getTypeface(context, "fonts/flutter_icon.otf");
        } else {
            throw new IllegalArgumentException("字体非法");
        }
        this.setTypeface(typeface);
        //去掉padding,这样iconfont和普通字体容易对齐
        setIncludeFontPadding(false);
        typedArray.recycle();
    }

    public synchronized Typeface getTypeface(Context context, String path) {
        if (ttfTypeface == null) {
            try {
                ttfTypeface = Typeface.createFromAsset(context.getAssets(), path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ttfTypeface;
    }

    public synchronized void clearTypeface() {
        ttfTypeface = null;
    }
}