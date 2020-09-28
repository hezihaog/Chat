package com.zh.android.base.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.zh.android.base.R;


/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib.utils <br>
 * <b>Create Date:</b> 2019/2/25  5:23 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 状态栏工具 <br>
 */
public class StatusBarUtil {
    private StatusBarUtil() {
    }

    /**
     * 设置状态栏字体黑色
     */
    public static void setStatusBarBlack(Activity activity) {
        QMUIStatusBarHelper.setStatusBarLightMode(activity);
    }

    /**
     * 设置状态栏字体白色
     */
    public static void setStatusBarWhite(Activity activity) {
        QMUIStatusBarHelper.setStatusBarDarkMode(activity);
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Activity activity) {
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return activity.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 设置状态栏透明
     */
    public static void setStatusBarTranslucent(Activity activity) {
        QMUIStatusBarHelper.translucent(activity);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = activity.getWindow();
//            window.setNavigationBarColor(Color.BLACK);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            View decorView = window.getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            //透明着色
//            //miui、flyme透明衔接，非miui、flyme加上阴影
//            if (RomUtil.isMiui() || RomUtil.isFlyme()) {
//                window.setStatusBarColor(Color.TRANSPARENT);
//            } else {
//                window.setStatusBarColor(Color.argb((int) (255 * 0.2f), 0, 0, 0));
//            }
//        }
    }

    /**
     * 取消透明状态栏
     */
    public static void cancelStatusBarTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setNavigationBarColor(Color.BLACK);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            View decorView = window.getDecorView();
//            int option = View.SYSTEM_UI_FLAG_VISIBLE;
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //着色
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.base_color_primary));
        }
    }

    /**
     * 非纯色状态栏可直接设置最外层控件的paddingTop
     */
    public static void setContainerPadding(Activity activity, View container) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            container.setPadding(0, getStatusBarHeight(activity), 0, 0);
        }
    }

    /**
     * 同setStatusBarHeight，可用setContainerPadding代替，不染色
     */
    public static void setStatusBarHeight(Activity activity, View statusBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams layoutParams = statusBar.getLayoutParams();
            layoutParams.height = getStatusBarHeight(activity);
            statusBar.setLayoutParams(layoutParams);
        }
    }

    /**
     * 纯色状态栏沉浸式
     *
     * @param statusBar 状态栏
     * @param color     状态栏颜色
     */
    public static void setStatusBarHeight(Activity activity, View statusBar, @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams layoutParams = statusBar.getLayoutParams();
            layoutParams.height = getStatusBarHeight(activity);
            statusBar.setLayoutParams(layoutParams);
            statusBar.setBackgroundColor(ContextCompat.getColor(activity, color));
        }
    }

    /**
     * 显示状态栏
     */
    public static void showStatusBar(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(attrs);
        }
    }

    /**
     * 隐藏状态栏
     */
    public static void hideStatusBar(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(attrs);
        }
    }
}