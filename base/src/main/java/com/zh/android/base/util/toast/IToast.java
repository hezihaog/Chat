package com.zh.android.base.util.toast;

import android.content.Context;

/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib.utils.toast <br>
 * <b>Create Date:</b> 2019-06-12  16:15 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 吐司接口，定义封装后的ToastApi <br>
 */
public interface IToast {
    /**
     * 以字符串资源Id，显示短Toast
     */
    void showMsg(Context context, int str);

    /**
     * 以字符串，显示短Toast
     */
    void showMsg(Context context, String str);

    /**
     * 以字符串资源Id，显示长Toast
     */
    void showMsgLong(Context context, int str);

    /**
     * 以字符串，显示长Toast
     */
    void showMsgLong(Context context, String str);
}