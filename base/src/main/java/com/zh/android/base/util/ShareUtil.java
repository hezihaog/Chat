package com.zh.android.base.util;

import android.app.Activity;
import android.content.Intent;

/**
 * @author wally
 * @date 2020/09/19
 * 系统分享工具类
 */
public class ShareUtil {
    /**
     * 系统分享，分享文字
     */
    public static void shareText(Activity activity, String text) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent = Intent.createChooser(shareIntent, "请选择您要分享到的应用");
        activity.startActivity(shareIntent);
    }
}