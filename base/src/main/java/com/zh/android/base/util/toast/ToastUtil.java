package com.zh.android.base.util.toast;

import android.content.Context;


/**
 * <b>Project:</b> LinghitPay <br>
 * <b>Create Date:</b> 2018/11/8 <br>
 * <b>@author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> toast 工具 <br>
 */
public class ToastUtil {
    /**
     * 吐司方案实现
     */
    private static IToast mToastImpl = new DefaultToastImpl();

    public static void showMsg(Context context, int str) {
        mToastImpl.showMsg(context, str);
    }

    public static void showMsg(Context context, String str) {
        if (str == null) {
            str = "";
        }
        mToastImpl.showMsg(context, str);
    }

    public static void showMsgLong(Context context, int str) {
        mToastImpl.showMsgLong(context, str);
    }

    public static void showMsgLong(Context context, String str) {
        if (str == null) {
            str = "";
        }
        mToastImpl.showMsgLong(context, str);
    }
}
