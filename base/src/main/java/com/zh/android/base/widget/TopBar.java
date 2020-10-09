package com.zh.android.base.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import com.zh.android.base.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 修改自QMUI的QMUITopBar，增加了几个自定义属性
 * <p>
 * 通用的顶部 Bar。提供了以下功能：
 * <p>
 * <ul>
 * <li>在左侧/右侧添加图片按钮/文字按钮/自定义 {@link View}。</li>
 * <li>设置标题/副标题，且支持设置标题/副标题的水平对齐方式。</li>
 * </ul>
 */
public class TopBar extends RelativeLayout {
    private static final int DEFAULT_VIEW_ID = -1;
    private int mLeftLastViewId; // 左侧最右 view 的 id
    private int mRightLastViewId; // 右侧最左 view 的 id

    private View mCenterView; // 中间的 View
    private LinearLayout mTitleContainerView; // 包裹 title 和 subTitle 的容器
    private TextView mTitleView; // 显示 title 文字的 TextView
    private TextView mSubTitleView; // 显示 subTitle 文字的 TextView

    private List<View> mLeftViewList;
    private List<View> mRightViewList;

    private boolean hasSeparator;
    private int mTopBarSeparatorColor;
    private int mTopBarBgColor;
    private int mTopBarSeparatorHeight;

    private Drawable mTopBarBgWithSeparatorDrawableCache;

    private int mTitleGravity;
    private int mLeftBackDrawableRes;
    private int mTitleTextSize;
    private int mTitleTextSizeWithSubTitle;
    private int mSubTitleTextSize;
    private int mTitleTextColor;
    private int mSubTitleTextColor;
    private int mTitleMarginHorWhenNoBtnAside;
    private int mTitleContainerPaddingHor;
    private int mTopBarImageBtnWidth;
    private int mTopBarImageBtnHeight;
    private int mTopBarTextBtnPaddingHor;
    private ColorStateList mTopBarTextBtnTextColor;
    private int mTopBarTextBtnTextSize;
    private int mTopBarHeight = -1;
    private Rect mTitleContainerRect;
    private String mTitleText;
    private String mSubTitleText;
    private Drawable mItemBackground;

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVar();
        init(context, attrs, 0);
    }

    // ========================= title 相关的方法

    private void initVar() {
        mLeftLastViewId = DEFAULT_VIEW_ID;
        mRightLastViewId = DEFAULT_VIEW_ID;
        mLeftViewList = new ArrayList<>();
        mRightViewList = new ArrayList<>();
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.TopBar, defStyleAttr, 0);
        mTopBarSeparatorColor = array.getColor(R.styleable.TopBar_topbar_separator_color, Color.parseColor("#DEE0E2"));
        mTopBarSeparatorHeight = array.getDimensionPixelSize(R.styleable.TopBar_topbar_separator_height, 1);
        mTopBarBgColor = array.getColor(R.styleable.TopBar_topbar_bg_color, Color.WHITE);
        getCommonFieldFormTypedArray(context, array);
        //是否有底部的一个分割线
        hasSeparator = array.getBoolean(R.styleable.TopBar_topbar_need_separator, false);
        array.recycle();
        setBackgroundDividerEnabled(hasSeparator);
        if (!TextUtils.isEmpty(mTitleText)) {
            setTitle(mTitleText);
        }
        if (!TextUtils.isEmpty(mSubTitleText)) {
            setSubTitle(mSubTitleText);
        }
        //?attr/selectableItemBackground
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        int[] attribute = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(typedValue.resourceId, attribute);
        mItemBackground = typedArray.getDrawable(0);
        typedArray.recycle();
    }

    void getCommonFieldFormTypedArray(Context context, TypedArray array) {
        mTopBarHeight = array.getDimensionPixelSize(R.styleable.TopBar_topbar_height, dp2px(context, 48));
        //标题
        mTitleText = array.getString(R.styleable.TopBar_topbar_title_text);
        //子标题
        mSubTitleText = array.getString(R.styleable.TopBar_topbar_sub_title_text);
        //默认左边的返回键
        mLeftBackDrawableRes = array.getResourceId(R.styleable.TopBar_topbar_left_back_drawable_id, R.id.topbar_item_back);
        mTitleGravity = array.getInt(R.styleable.TopBar_topbar_title_gravity, Gravity.CENTER);
        mTitleTextSize = array.getDimensionPixelSize(R.styleable.TopBar_topbar_title_text_size, sp2px(context, 17));
        mTitleTextSizeWithSubTitle = array.getDimensionPixelSize(R.styleable.TopBar_topbar_title_text_size, sp2px(context, 16));
        mSubTitleTextSize = array.getDimensionPixelSize(R.styleable.TopBar_topbar_subtitle_text_size, sp2px(context, 11));
        mTitleTextColor = array.getColor(R.styleable.TopBar_topbar_title_color, Color.parseColor("#353C46"));
        mSubTitleTextColor = array.getColor(R.styleable.TopBar_topbar_subtitle_color, getAttrColor(context, Color.parseColor("#717882")));
        mTitleMarginHorWhenNoBtnAside = array.getDimensionPixelSize(R.styleable.TopBar_topbar_title_margin_horizontal_when_no_btn_aside, 0);
        mTitleContainerPaddingHor = array.getDimensionPixelSize(R.styleable.TopBar_topbar_title_container_padding_horizontal, 0);
        mTopBarImageBtnWidth = array.getDimensionPixelSize(R.styleable.TopBar_topbar_image_btn_width, dp2px(context, 48));
        mTopBarImageBtnHeight = array.getDimensionPixelSize(R.styleable.TopBar_topbar_image_btn_height, dp2px(context, 48));
        mTopBarTextBtnPaddingHor = array.getDimensionPixelSize(R.styleable.TopBar_topbar_text_btn_padding_horizontal, dp2px(context, 12));
        mTopBarTextBtnTextColor = array.getColorStateList(R.styleable.TopBar_topbar_text_btn_color_state_list);
        mTopBarTextBtnTextSize = array.getDimensionPixelSize(R.styleable.TopBar_topbar_text_btn_text_size, sp2px(context, 16));
    }

    /**
     * 在 TopBar 的中间添加 View，如果此前已经有 View 通过该方法添加到 TopBar，则旧的View会被 remove
     *
     * @param view 要添加到TopBar中间的View
     */
    public void setCenterView(View view) {
        if (mCenterView == view) {
            return;
        }
        if (mCenterView != null) {
            removeView(mCenterView);
        }
        mCenterView = view;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCenterView.getLayoutParams();
        if (params == null) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(view, params);
    }

    public View getCenterView() {
        return mCenterView;
    }

    /**
     * 添加 TopBar 的标题
     *
     * @param resId TopBar 的标题 resId
     */
    public TextView setTitle(int resId) {
        return setTitle(getContext().getString(resId));
    }

    /**
     * 添加 TopBar 的标题
     *
     * @param title TopBar 的标题
     */
    public TextView setTitle(String title) {
        TextView titleView = getTitleView(false);
        titleView.setText(title);
        if (isNullOrEmpty(title)) {
            titleView.setVisibility(GONE);
        } else {
            titleView.setVisibility(VISIBLE);
        }
        return titleView;
    }

    /**
     * 设置标题字体颜色
     */
    public void setTitleTextColor(int color) {
        TextView titleView = getTitleView(false);
        titleView.setTextColor(color);
    }

    public void clearTitleText() {
        setTitle("");
    }

    public CharSequence getTitle() {
        if (mTitleView == null) {
            return null;
        }
        return mTitleView.getText();
    }

    public TextView setEmojiTitle(String title) {
        TextView titleView = getTitleView(true);
        titleView.setText(title);
        if (isNullOrEmpty(title)) {
            titleView.setVisibility(GONE);
        } else {
            titleView.setVisibility(VISIBLE);
        }
        return titleView;
    }

    public void showTitleView(boolean toShow) {
        if (mTitleView != null) {
            mTitleView.setVisibility(toShow ? VISIBLE : GONE);
        }
    }

    private TextView getTitleView(boolean isEmoji) {
        if (mTitleView == null) {
//            mTitleView = isEmoji ? new EmojiconTextView(getContext()) : new TextView(getContext());
            mTitleView = new TextView(getContext());
            mTitleView.setGravity(Gravity.CENTER);
            mTitleView.setSingleLine(true);
            mTitleView.setEllipsize(TruncateAt.MIDDLE);
            mTitleView.setTextColor(mTitleTextColor);
            updateTitleViewStyle();
            LinearLayout.LayoutParams titleLp = generateTitleViewAndSubTitleViewLp();
            makeSureTitleContainerView().addView(mTitleView, titleLp);
        }

        return mTitleView;
    }

    /**
     * 更新 titleView 的样式（因为有没有 subTitle 会影响 titleView 的样式）
     */
    private void updateTitleViewStyle() {
        if (mTitleView != null) {
            if (mSubTitleView == null || isNullOrEmpty(mSubTitleView.getText())) {
                mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize);
            } else {
                mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSizeWithSubTitle);
            }
        }
    }

    /**
     * 添加 TopBar 的副标题
     *
     * @param subTitle TopBar 的副标题
     */
    public void setSubTitle(String subTitle) {
        TextView titleView = getSubTitleView();
        titleView.setText(subTitle);
        if (isNullOrEmpty(subTitle)) {
            titleView.setVisibility(GONE);
        } else {
            titleView.setVisibility(VISIBLE);
        }
        // 更新 titleView 的样式（因为有没有 subTitle 会影响 titleView 的样式）
        updateTitleViewStyle();
    }

    /**
     * 添加 TopBar 的副标题
     *
     * @param resId TopBar 的副标题 resId
     */
    public void setSubTitle(int resId) {
        setSubTitle(getResources().getString(resId));
    }

    private TextView getSubTitleView() {
        if (mSubTitleView == null) {
            mSubTitleView = new TextView(getContext());
            mSubTitleView.setGravity(Gravity.CENTER);
            mSubTitleView.setSingleLine(true);
            mSubTitleView.setEllipsize(TruncateAt.MIDDLE);
            mSubTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSubTitleTextSize);
            mSubTitleView.setTextColor(mSubTitleTextColor);
            LinearLayout.LayoutParams titleLp = generateTitleViewAndSubTitleViewLp();
            titleLp.topMargin = dp2px(getContext(), 1);
            makeSureTitleContainerView().addView(mSubTitleView, titleLp);
        }

        return mSubTitleView;
    }

    /**
     * 设置 TopBar 的 gravity，用于控制 title 和 subtitle 的对齐方式
     *
     * @param gravity 参考 {@link Gravity}
     */
    public void setTitleGravity(int gravity) {
        mTitleGravity = gravity;
        if (mTitleView != null) {
            ((LinearLayout.LayoutParams) mTitleView.getLayoutParams()).gravity = gravity;
            if (gravity == Gravity.CENTER || gravity == Gravity.CENTER_HORIZONTAL) {
                mTitleView.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingLeft(), getPaddingBottom());
            }
        }
        if (mSubTitleView != null) {
            ((LinearLayout.LayoutParams) mSubTitleView.getLayoutParams()).gravity = gravity;
        }
        requestLayout();
    }

    public Rect getTitleContainerRect() {
        if (mTitleContainerRect == null) {
            mTitleContainerRect = new Rect();
        }
        if (mTitleContainerView == null) {
            mTitleContainerRect.set(0, 0, 0, 0);
        } else {
            getDescendantRect(this, mTitleContainerView, mTitleContainerRect);
        }
        return mTitleContainerRect;
    }


    // ========================= leftView、rightView 相关的方法

    private LinearLayout makeSureTitleContainerView() {
        if (mTitleContainerView == null) {
            mTitleContainerView = new LinearLayout(getContext());
            // 垂直，后面要支持水平的话可以加个接口来设置
            mTitleContainerView.setOrientation(LinearLayout.VERTICAL);
            mTitleContainerView.setGravity(Gravity.CENTER);
            mTitleContainerView.setPadding(mTitleContainerPaddingHor, 0, mTitleContainerPaddingHor, 0);
            addView(mTitleContainerView, generateTitleContainerViewLp());
        }
        return mTitleContainerView;
    }

    /**
     * 生成 TitleContainerView 的 LayoutParams。
     * 左右有按钮时，该 View 在左右按钮之间；
     * 没有左右按钮时，该 View 距离 TopBar 左右边缘有固定的距离
     */
    private RelativeLayout.LayoutParams generateTitleContainerViewLp() {
        return new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dp2px(getContext(), 56));
    }

    /**
     * 生成 titleView 或 subTitleView 的 LayoutParams
     */
    private LinearLayout.LayoutParams generateTitleViewAndSubTitleViewLp() {
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // 垂直居中
        titleLp.gravity = mTitleGravity;
        return titleLp;
    }

    /**
     * 在TopBar的左侧添加View，如果此前已经有View通过该方法添加到TopBar，则新添加进去的View会出现在已有View的右侧
     *
     * @param view   要添加到 TopBar 左边的 View
     * @param viewId 该按钮的id，可在ids.xml中找到合适的或新增。手工指定viewId是为了适应自动化测试。
     */
    public void addLeftView(View view, int viewId) {
        ViewGroup.LayoutParams viewLayoutParams = view.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams;
        if (viewLayoutParams != null && viewLayoutParams instanceof RelativeLayout.LayoutParams) {
            layoutParams = (RelativeLayout.LayoutParams) viewLayoutParams;
        } else {
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        this.addLeftView(view, viewId, layoutParams);
    }

    /**
     * 在TopBar的左侧添加View，如果此前已经有View通过该方法添加到TopBar，则新添加进去的View会出现在已有View的右侧。
     *
     * @param view         要添加到 TopBar 左边的 View。
     * @param viewId       该按钮的 id，可在 ids.xml 中找到合适的或新增。手工指定 viewId 是为了适应自动化测试。
     * @param layoutParams 传入一个 LayoutParams，当把 Button addView 到 TopBar 时，使用这个 LayouyParams。
     */
    public void addLeftView(View view, int viewId, RelativeLayout.LayoutParams layoutParams) {
        if (mLeftLastViewId == DEFAULT_VIEW_ID) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else {
            layoutParams.addRule(RelativeLayout.RIGHT_OF, mLeftLastViewId);
        }
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.alignWithParent = true; // alignParentIfMissing
        mLeftLastViewId = viewId;
        view.setId(viewId);
        mLeftViewList.add(view);
        addView(view, layoutParams);
    }

    /**
     * 在 TopBar 的右侧添加 View，如果此前已经有 iew 通过该方法添加到 TopBar，则新添加进去的View会出现在已有View的左侧
     *
     * @param view   要添加到 TopBar 右边的View
     * @param viewId 该按钮的id，可在 ids.xml 中找到合适的或新增。手工指定 viewId 是为了适应自动化测试。
     */
    public void addRightView(View view, int viewId) {
        ViewGroup.LayoutParams viewLayoutParams = view.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams;
        if (viewLayoutParams != null && viewLayoutParams instanceof RelativeLayout.LayoutParams) {
            layoutParams = (RelativeLayout.LayoutParams) viewLayoutParams;
        } else {
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        this.addRightView(view, viewId, layoutParams);
    }

    /**
     * 在 TopBar 的右侧添加 View，如果此前已经有 View 通过该方法添加到 TopBar，则新添加进去的 View 会出现在已有View的左侧。
     *
     * @param view         要添加到 TopBar 右边的 View。
     * @param viewId       该按钮的 id，可在 ids.xml 中找到合适的或新增。手工指定 viewId 是为了适应自动化测试。
     * @param layoutParams 生成一个 LayoutParams，当把 Button addView 到 TopBar 时，使用这个 LayouyParams。
     */
    public void addRightView(View view, int viewId, RelativeLayout.LayoutParams layoutParams) {
        if (mRightLastViewId == DEFAULT_VIEW_ID) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else {
            layoutParams.addRule(RelativeLayout.LEFT_OF, mRightLastViewId);
        }
        layoutParams.alignWithParent = true; // alignParentIfMissing
        mRightLastViewId = viewId;
        view.setId(viewId);
        mRightViewList.add(view);
        addView(view, layoutParams);
    }

    /**
     * 生成一个 LayoutParams，当把 Button addView 到 TopBar 时，使用这个 LayouyParams
     */
    public RelativeLayout.LayoutParams generateTopBarImageButtonLayoutParams() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mTopBarImageBtnWidth, mTopBarImageBtnHeight);
        lp.topMargin = Math.max(0, (getTopBarHeight() - mTopBarImageBtnHeight) / 2);
        return lp;
    }

    /**
     * 根据 resourceId 生成一个 TopBar 的按钮，并 add 到 TopBar 的右侧
     *
     * @param drawableResId 按钮图片的 resourceId
     * @param viewId        该按钮的 id，可在 ids.xml 中找到合适的或新增。手工指定 viewId 是为了适应自动化测试。
     * @return 返回生成的按钮
     */
    public ImageView addRightImageButton(int drawableResId, int viewId) {
        ImageButton rightButton = generateTopBarImageButton(drawableResId);
        this.addRightView(rightButton, viewId, generateTopBarImageButtonLayoutParams());
        return rightButton;
    }

    /**
     * 获取图片按钮宽度
     */
    public int getImageBtnWidth() {
        return mTopBarImageBtnWidth;
    }

    /**
     * 添加已有View的按钮到右侧
     *
     * @param rightButton 已有的View
     */
    public ImageButton addRightImageButton(ImageButton rightButton) {
        //如果有父类统一先移除
        if (rightButton.getParent() != null) {
            ((ViewGroup) rightButton.getParent()).removeView(rightButton);
        }
        this.addRightView(rightButton, rightButton.getId(), generateTopBarImageButtonLayoutParams());
        return rightButton;
    }

    /**
     * 根据 resourceId 生成一个 TopBar 的按钮，并 add 到 TopBar 的左边
     *
     * @param drawableResId 按钮图片的 resourceId
     * @param viewId        该按钮的 id，可在ids.xml中找到合适的或新增。手工指定 viewId 是为了适应自动化测试。
     * @return 返回生成的按钮
     */
    public ImageButton addLeftImageButton(int drawableResId, int viewId) {
        ImageButton leftButton = generateTopBarImageButton(drawableResId);
        this.addLeftView(leftButton, viewId, generateTopBarImageButtonLayoutParams());
        return leftButton;
    }

    public ImageView addLeftImageView(int drawableResId, int viewId) {
        ImageView leftView = generateTopBarImageView(drawableResId);
        this.addLeftView(leftView, viewId, generateTopBarImageButtonLayoutParams());
        return leftView;
    }

    /**
     * 添加一个已有的View到左侧
     *
     * @param leftButton 已有的View
     */
    public ImageButton addLeftImageButton(ImageButton leftButton) {
        if (leftButton.getParent() != null) {
            ((ViewGroup) leftButton.getParent()).removeView(leftButton);
        }
        this.addLeftView(leftButton, leftButton.getId(), generateTopBarImageButtonLayoutParams());
        return leftButton;
    }

    /**
     * 生成一个LayoutParams，当把 Button addView 到 TopBar 时，使用这个 LayouyParams
     */
    public RelativeLayout.LayoutParams generateTopBarTextButtonLayoutParams() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, mTopBarImageBtnHeight);
        lp.topMargin = Math.max(0, (getTopBarHeight() - mTopBarImageBtnHeight) / 2);
        return lp;
    }

    /**
     * 在 TopBar 左边添加一个 Button，并设置文字
     *
     * @param stringResId 按钮的文字的 resourceId
     * @param viewId      该按钮的id，可在 ids.xml 中找到合适的或新增。手工指定 viewId 是为了适应自动化测试。
     * @return 返回生成的按钮
     */
    public Button addLeftTextButton(int stringResId, int viewId) {
        return addLeftTextButton(getResources().getString(stringResId), viewId);
    }

    /**
     * 在 TopBar 左边添加一个 Button，并设置文字
     *
     * @param buttonText 按钮的文字
     * @param viewId     该按钮的 id，可在 ids.xml 中找到合适的或新增。手工指定 viewId 是为了适应自动化测试。
     * @return 返回生成的按钮
     */
    public Button addLeftTextButton(String buttonText, int viewId) {
        Button button = generateTopBarTextButton(buttonText);
        this.addLeftView(button, viewId, generateTopBarTextButtonLayoutParams());
        return button;
    }

    /**
     * 在 TopBar 右边添加一个 Button，并设置文字
     *
     * @param stringResId 按钮的文字的 resourceId
     * @param viewId      该按钮的id，可在 ids.xml 中找到合适的或新增。手工指定 viewId 是为了适应自动化测试。
     * @return 返回生成的按钮
     */
    public Button addRightTextButton(int stringResId, int viewId) {
        return addRightTextButton(getResources().getString(stringResId), viewId);
    }

    /**
     * 在 TopBar 右边添加一个 Button，并设置文字
     *
     * @param buttonText 按钮的文字
     * @param viewId     该按钮的 id，可在 ids.xml 中找到合适的或新增。手工指定 viewId 是为了适应自动化测试。
     * @return 返回生成的按钮
     */
    public Button addRightTextButton(String buttonText, int viewId) {
        Button button = generateTopBarTextButton(buttonText);
        this.addRightView(button, viewId, generateTopBarTextButtonLayoutParams());
        return button;
    }

    /**
     * 生成一个文本按钮，并设置文字
     *
     * @param text 按钮的文字
     * @return 返回生成的按钮
     */
    private Button generateTopBarTextButton(String text) {
        Button button = new Button(getContext());
        if (mItemBackground != null) {
            button.setBackground(mItemBackground.getConstantState().newDrawable());
        } else {
            button.setBackgroundResource(0);
        }
        button.setMinWidth(0);
        button.setMinHeight(0);
        button.setMinimumWidth(0);
        button.setMinimumHeight(0);
        button.setPadding(mTopBarTextBtnPaddingHor, 0, mTopBarTextBtnPaddingHor, 0);
        button.setTextColor(mTopBarTextBtnTextColor);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTopBarTextBtnTextSize);
        button.setGravity(Gravity.CENTER);
        button.setText(text);
        return button;
    }

    /**
     * 生成一个图片按钮，配合 {{@link #generateTopBarImageButtonLayoutParams()} 使用
     *
     * @param imageResourceId 图片的 resId
     */
    private ImageButton generateTopBarImageButton(int imageResourceId) {
        ImageButton backButton = new ImageButton(getContext());
        if (mItemBackground != null) {
            backButton.setBackground(mItemBackground.getConstantState().newDrawable());
        } else {
            backButton.setBackgroundColor(Color.TRANSPARENT);
        }
        backButton.setImageResource(imageResourceId);
        return backButton;
    }

    private ImageView generateTopBarImageView(int imageResourceId) {
        ImageView backView = new ImageView(getContext());
        if (mItemBackground != null) {
            backView.setBackground(mItemBackground.getConstantState().newDrawable());
        } else {
            backView.setBackgroundColor(Color.TRANSPARENT);
        }
        backView.setImageResource(imageResourceId);
        return backView;
    }

    /**
     * 便捷方法，在 TopBar 左边添加一个返回图标按钮
     *
     * @return 返回按钮
     */
    public ImageButton addLeftBackImageButton() {
        return addLeftImageButton(mLeftBackDrawableRes, R.id.topbar_item_back);
    }

    /**
     * 在 TopBar 左边添加一个返回图标按钮，类型是ImageView，由于某些图片比较大可能超出ImageButton的边界产生裁切
     * 所以才加这个方法
     */
    public ImageView addLeftBackImageView() {
        return addLeftImageView(mLeftBackDrawableRes, R.id.topbar_item_back);
    }

    /**
     * 外部定制返回键资源
     */
    public ImageButton addLeftBackImageButton(int drawableResId) {
        return addLeftImageButton(drawableResId, R.id.topbar_item_back);
    }

    /**
     * 移除 TopBar 左边所有的 View
     */
    public void removeAllLeftViews() {
        for (View leftView : mLeftViewList) {
            removeView(leftView);
        }
        mLeftLastViewId = DEFAULT_VIEW_ID;
        mLeftViewList.clear();
    }

    /**
     * 移除 TopBar 右边所有的 View
     */
    public void removeAllRightViews() {
        for (View rightView : mRightViewList) {
            removeView(rightView);
        }
        mRightLastViewId = DEFAULT_VIEW_ID;
        mRightViewList.clear();
    }

    /**
     * 获取右边的View数量
     */
    public int getRightViewCount() {
        if (mRightViewList == null) {
            return 0;
        } else {
            return mRightViewList.size();
        }
    }

    /**
     * 移除 TopBar 的 centerView 和 titleView
     */
    public void removeCenterViewAndTitleView() {
        if (mCenterView != null) {
            if (mCenterView.getParent() == this) {
                removeView(mCenterView);
            }
            mCenterView = null;
        }

        if (mTitleView != null) {
            if (mTitleView.getParent() == this) {
                removeView(mTitleView);
            }
            mTitleView = null;
        }
    }

    private int getTopBarHeight() {
        if (mTopBarHeight == -1) {
            mTopBarHeight = dp2px(getContext(), 48);
        }
        return mTopBarHeight;
    }

    // ======================== TopBar自身相关的方法

    /**
     * 设置 TopBar 背景的透明度
     *
     * @param alpha 取值范围：[0, 255]，255表示不透明
     */
    public void setBackgroundAlpha(int alpha) {
        this.getBackground().setAlpha(alpha);
    }

    /**
     * 根据当前 offset、透明度变化的初始 offset 和目标 offset，计算并设置 Topbar 的透明度
     *
     * @param currentOffset     当前 offset
     * @param alphaBeginOffset  透明度开始变化的offset，即当 currentOffset == alphaBeginOffset 时，透明度为0
     * @param alphaTargetOffset 透明度变化的目标offset，即当 currentOffset == alphaTargetOffset 时，透明度为1
     */
    public int computeAndSetBackgroundAlpha(int currentOffset, int alphaBeginOffset, int alphaTargetOffset) {
        double alpha = (float) (currentOffset - alphaBeginOffset) / (alphaTargetOffset - alphaBeginOffset);
        alpha = Math.max(0, Math.min(alpha, 1)); // from 0 to 1
        int alphaInt = (int) (alpha * 255);
        this.setBackgroundAlpha(alphaInt);
        return alphaInt;
    }

    /**
     * 设置背景
     */
    public void setTopBarBackgroundColor(int color) {
        mTopBarBgColor = color;
        setBackgroundDividerEnabled(hasSeparator);
    }

    /**
     * 设置是否要 Topbar 底部的分割线
     */
    public void setBackgroundDividerEnabled(boolean enabled) {
        if (enabled) {
            if (mTopBarBgWithSeparatorDrawableCache == null) {
                mTopBarBgWithSeparatorDrawableCache =
                        createItemSeparatorBg(mTopBarSeparatorColor, mTopBarBgColor, mTopBarSeparatorHeight, false);
            }
            setBackgroundKeepingPadding(this, mTopBarBgWithSeparatorDrawableCache);
        } else {
            setBackgroundColorKeepPadding(this, mTopBarBgColor);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTitleContainerView != null) {
            // 计算左侧 View 的总宽度
            int leftViewWidth = 0;
            for (int leftViewIndex = 0; leftViewIndex < mLeftViewList.size(); leftViewIndex++) {
                View view = mLeftViewList.get(leftViewIndex);
                if (view.getVisibility() != GONE) {
                    leftViewWidth += view.getMeasuredWidth();
                }
            }
            // 计算右侧 View 的总宽度
            int rightViewWidth = 0;
            for (int rightViewIndex = 0; rightViewIndex < mRightViewList.size(); rightViewIndex++) {
                View view = mRightViewList.get(rightViewIndex);
                if (view.getVisibility() != GONE) {
                    rightViewWidth += view.getMeasuredWidth();
                }
            }
            // 计算 titleContainer 的最大宽度
            int titleContainerWidth;
            if ((mTitleGravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {
                if (leftViewWidth == 0 && rightViewWidth == 0) {
                    // 左右没有按钮时，title 距离 TopBar 左右边缘的距离
                    leftViewWidth += mTitleMarginHorWhenNoBtnAside;
                    rightViewWidth += mTitleMarginHorWhenNoBtnAside;
                }

                // 标题水平居中，左右两侧的占位要保持一致
                titleContainerWidth = View.MeasureSpec.getSize(widthMeasureSpec) - Math.max(leftViewWidth, rightViewWidth) * 2 - getPaddingLeft() - getPaddingRight();
            } else {
                // 标题非水平居中，左右没有按钮时，间距分别计算
                if (leftViewWidth == 0) {
                    leftViewWidth += mTitleMarginHorWhenNoBtnAside;
                }
                if (rightViewWidth == 0) {
                    rightViewWidth += mTitleMarginHorWhenNoBtnAside;
                }

                // 标题非水平居中，左右两侧的占位按实际计算即可
                titleContainerWidth = View.MeasureSpec.getSize(widthMeasureSpec) - leftViewWidth - rightViewWidth - getPaddingLeft() - getPaddingRight();
            }
            int titleContainerWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(titleContainerWidth, View.MeasureSpec.EXACTLY);
            mTitleContainerView.measure(titleContainerWidthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mTitleContainerView != null) {
            int titleContainerViewWidth = mTitleContainerView.getMeasuredWidth();
            int titleContainerViewHeight = mTitleContainerView.getMeasuredHeight();
            int titleContainerViewTop = (b - t - mTitleContainerView.getMeasuredHeight()) / 2;
            int titleContainerViewLeft = getPaddingLeft();
            if ((mTitleGravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {
                // 标题水平居中
                titleContainerViewLeft = (r - l - mTitleContainerView.getMeasuredWidth()) / 2;
            } else {
                // 标题非水平居中
                // 计算左侧 View 的总宽度
                for (int leftViewIndex = 0; leftViewIndex < mLeftViewList.size(); leftViewIndex++) {
                    View view = mLeftViewList.get(leftViewIndex);
                    if (view.getVisibility() != GONE) {
                        titleContainerViewLeft += view.getMeasuredWidth();
                    }
                }

                if (mLeftViewList.isEmpty()) {
                    //左侧没有按钮，标题离左侧间距
                    titleContainerViewLeft += getAttrDimen(getContext(),
                            R.attr.topbar_title_margin_horizontal_when_no_btn_aside);
                }
            }
            mTitleContainerView.layout(titleContainerViewLeft, titleContainerViewTop, titleContainerViewLeft + titleContainerViewWidth, titleContainerViewTop + titleContainerViewHeight);
        }
    }

    public int getTitleMarginHorWhenNoBtnAside() {
        return mTitleMarginHorWhenNoBtnAside;
    }

    public static boolean isNullOrEmpty(@Nullable CharSequence string) {
        return string == null || string.length() == 0;
    }

    /**
     * Retrieve the transformed bounding rect of an arbitrary descendant view.
     * This does not need to be a direct child.
     *
     * @param descendant descendant view to reference
     * @param out        rect to set to the bounds of the descendant view
     */
    public static void getDescendantRect(ViewGroup parent, View descendant, Rect out) {
        out.set(0, 0, descendant.getWidth(), descendant.getHeight());
        offsetDescendantRect(parent, descendant, out);
    }

    private static final ThreadLocal<Matrix> sMatrix = new ThreadLocal<>();
    private static final ThreadLocal<RectF> sRectF = new ThreadLocal<>();

    public static void offsetDescendantRect(ViewGroup group, View child, Rect rect) {
        Matrix m = sMatrix.get();
        if (m == null) {
            m = new Matrix();
            sMatrix.set(m);
        } else {
            m.reset();
        }

        offsetDescendantMatrix(group, child, m);

        RectF rectF = sRectF.get();
        if (rectF == null) {
            rectF = new RectF();
            sRectF.set(rectF);
        }
        rectF.set(rect);
        m.mapRect(rectF);
        rect.set((int) (rectF.left + 0.5f), (int) (rectF.top + 0.5f),
                (int) (rectF.right + 0.5f), (int) (rectF.bottom + 0.5f));
    }

    static void offsetDescendantMatrix(ViewParent target, View view, Matrix m) {
        final ViewParent parent = view.getParent();
        if (parent instanceof View && parent != target) {
            final View vp = (View) parent;
            offsetDescendantMatrix(target, vp, m);
            m.preTranslate(-vp.getScrollX(), -vp.getScrollY());
        }

        m.preTranslate(view.getLeft(), view.getTop());

        if (!view.getMatrix().isIdentity()) {
            m.preConcat(view.getMatrix());
        }
    }

    /**
     * 动态创建带上分隔线或下分隔线的Drawable。
     *
     * @param separatorColor 分割线颜色。
     * @param bgColor        Drawable 的背景色。
     * @param top            true 则分割线为上分割线，false 则为下分割线。
     * @return 返回所创建的 Drawable。
     */
    public static LayerDrawable createItemSeparatorBg(@ColorInt int separatorColor, @ColorInt int bgColor, int separatorHeight, boolean top) {
        ShapeDrawable separator = new ShapeDrawable();
        separator.getPaint().setStyle(Paint.Style.FILL);
        separator.getPaint().setColor(separatorColor);

        ShapeDrawable bg = new ShapeDrawable();
        bg.getPaint().setStyle(Paint.Style.FILL);
        bg.getPaint().setColor(bgColor);

        Drawable[] layers = {separator, bg};
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        layerDrawable.setLayerInset(1, 0, top ? separatorHeight : 0, 0, top ? 0 : separatorHeight);
        return layerDrawable;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static void setBackgroundKeepingPadding(View view, Drawable drawable) {
        int[] padding = new int[]{view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};
        setBackground(view, drawable);
        view.setPadding(padding[0], padding[1], padding[2], padding[3]);
    }

    @SuppressWarnings("deprecation")
    public static void setBackgroundKeepingPadding(View view, int backgroundResId) {
        setBackgroundKeepingPadding(view, ContextCompat.getDrawable(view.getContext(), backgroundResId));
    }

    public static void setBackgroundColorKeepPadding(View view, @ColorInt int color) {
        int[] padding = new int[]{view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};
        view.setBackgroundColor(color);
        view.setPadding(padding[0], padding[1], padding[2], padding[3]);
    }

    public static int getAttrColor(Context context, int attrRes) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue.data;
    }

    public static int getAttrDimen(Context context, int attrRes) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return TypedValue.complexToDimensionPixelSize(typedValue.data, getDisplayMetrics(context));
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static float getFontDensity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * 单位转换: dp -> px
     */
    public static int dp2px(Context context, int dp) {
        return (int) (getDensity(context) * dp + 0.5);
    }

    /**
     * 单位转换: sp -> px
     */
    public static int sp2px(Context context, int sp) {
        return (int) (getFontDensity(context) * sp + 0.5);
    }

    /**
     * 获取 DisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }
}