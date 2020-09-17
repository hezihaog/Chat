package com.zh.android.base.util;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.zh.android.contextprovider.ContextProvider;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * @author wally
 * @date 2020/09/17
 */
public class NotificationUtil {
    private NotificationUtil() {
    }

    /**
     * 判断应用的通知的开关是否开启
     */
    public static boolean checkNotificationEnable(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        //24直接用提供的Api
        if (Build.VERSION.SDK_INT >= 24) {
            return notificationManager.areNotificationsEnabled();
        } else if (Build.VERSION.SDK_INT >= 19) {
            //19以下反射获取
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            try {
                Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                int value = (Integer) opPostNotificationValue.get(Integer.class);
                return (Integer) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg) == 0;
            } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException | RuntimeException | ClassNotFoundException var9) {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * 跳转到系统通知栏设置界面
     */
    public static void goNotificationSetting(Context context) {
        Intent intent = new Intent();
        //假设没有开启通知权限，点击之后就需要跳转到 APP的通知设置界面
        //对应的Action是：Settings.ACTION_APP_NOTIFICATION_SETTINGS, 这个Action是 API 26 后增加的
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            //如果在部分手机中无法精确的跳转到APP对应的通知设置界面，那么我们就考虑直接跳转到 APP信息界面
            //对应的Action是：Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 创建一个通知
     *
     * @param id           通知id
     * @param channelId    通知渠道Id
     * @param channelName  通知渠道名称
     * @param intent       点击通知栏后要执行的Intent
     * @param smallIcon    通知栏图标
     * @param contentTitle 通知标题
     * @param contentText  通知内容
     * @param isNoClear    是否常驻通知栏
     * @param isHasSound   是否需要播放声音
     * @param isHasVibrate 是否需要震动
     * @param isHasLights  是否需要闪光灯
     */
    public static void create(Context context,
                              int id,
                              String channelId,
                              String channelName,
                              Intent intent,
                              int smallIcon,
                              String contentTitle,
                              String contentText,
                              boolean isNoClear,
                              boolean isHasSound,
                              boolean isHasVibrate,
                              boolean isHasLights) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //兼容8.0通知栏
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            //配置通知渠道
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
        //设置点击跳转的意图
        builder.setContentIntent(pendingIntent);
        //设置标题等
        builder.setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true);

        int defaults = 0;
        //是否强驻通知栏
        if (isNoClear) {
            defaults |= Notification.FLAG_NO_CLEAR;
        }
        //是否需要播放声音
        if (isHasSound) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        //是否需要震动
        if (isHasVibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        //是否需要闪光灯
        if (isHasLights) {
            defaults |= Notification.DEFAULT_LIGHTS;
        }
        builder.setDefaults(defaults);
        notificationManager.notify(id, builder.build());
    }

    /**
     * 取消指定通知id的通知
     *
     * @param id 通知id
     */
    public static void cancel(final int id) {
        NotificationManagerCompat.from(ContextProvider.get().getContext()).cancel(id);
    }

    /**
     * 取消所有通知
     */
    public static void cancelAll() {
        NotificationManagerCompat.from(ContextProvider.get().getContext()).cancelAll();
    }
}