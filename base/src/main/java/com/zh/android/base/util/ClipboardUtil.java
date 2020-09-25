package com.zh.android.base.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * @author wally
 * @date 2020/09/25
 * 剪切板工具类
 */
public class ClipboardUtil {
    private ClipboardUtil() {
    }

    /**
     * 拷贝文本到剪切板
     *
     * @param text 要拷贝的文本
     */
    public static void copyToClipboard(Context context, String text) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        //创建普通字符型ClipData
        ClipData clipData = ClipData.newPlainText("Label", text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(clipData);
    }
}