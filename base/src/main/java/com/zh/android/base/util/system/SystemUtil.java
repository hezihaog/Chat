package com.zh.android.base.util.system;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * <b>Package:</b> com.tongwei.smarttoilet.base.util <br>
 * <b>Create Date:</b> 2019-08-27  08:48 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class SystemUtil {
    private SystemUtil() {
    }

    /**
     * 获取当前应用的VersionName
     *
     * @param context 上下文
     */
    public static String getAppVersionName(Context context) {
        try {
            String packageName = context.getPackageName();
            return context.getPackageManager().getPackageInfo(
                    packageName, 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取App名字
     */
    public static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        return (String) packageManager.getApplicationLabel(applicationInfo);
    }
}