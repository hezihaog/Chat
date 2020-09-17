package com.zh.android.base.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Locale;

/**
 * @author wally
 * @date 2020/09/17
 * 设备Id工具类
 */
public class DeviceIdUtil {
    /**
     * 获取设备唯一Id
     */
    public static String getDeviceUniqueId(Context paramContext) {
        String str = "";
        if (Build.VERSION.SDK_INT < 23) {
            str = getDeviceId(paramContext);
            if (TextUtils.isEmpty(str)) {
                str = getMacAddressFromWifiManager(paramContext);
                if (TextUtils.isEmpty(str)) {
                    str = Settings.Secure.getString(paramContext.getContentResolver(), "android_id");
                    if (TextUtils.isEmpty(str)) {
                        str = getSerial();
                    }
                }
            }
        } else if (Build.VERSION.SDK_INT == 23) {
            str = getDeviceId(paramContext);
            if (TextUtils.isEmpty(str)) {
                str = getMacAddressFromNetworkInterface();
                if (TextUtils.isEmpty(str)) {
                    if (true) {//反编译看代码默认是true
                        str = getMacAddressFromFile();
                    } else {
                        str = getMacAddressFromWifiManager(paramContext);
                    }
                }
                if (TextUtils.isEmpty(str)) {
                    str = Settings.Secure.getString(paramContext.getContentResolver(), "android_id");
                    if (TextUtils.isEmpty(str)) {
                        str = getSerial();
                    }
                }
            }
        } else {
            str = getDeviceId(paramContext);
            if (TextUtils.isEmpty(str)) {
                str = getSerial();
                if (TextUtils.isEmpty(str)) {
                    str = Settings.Secure.getString(paramContext.getContentResolver(), "android_id");
                    if (TextUtils.isEmpty(str)) {
                        str = getMacAddressFromNetworkInterface();
                        if (TextUtils.isEmpty(str)) {
                            str = getMacAddressFromWifiManager(paramContext);
                        }
                    }
                }
            }
        }
        return str;
    }

    private static String getMacAddressFromFile() {
        try {
            String[] arrayOfString = {"/sys/class/net/wlan0/address", "/sys/class/net/eth0/address", "/sys/devices/virtual/net/wlan0/address"};
            for (byte b1 = 0; b1 < arrayOfString.length; b1++) {
                try {
                    String str = a(arrayOfString[b1]);
                    if (str != null) {
                        return str;
                    }
                } catch (Throwable throwable) {
                }
            }
        } catch (Throwable throwable) {
        }
        return null;
    }

    private static String a(String paramString) {
        String str = null;
        try {
            FileReader fileReader = new FileReader(paramString);
            BufferedReader bufferedReader = null;
            if (fileReader != null) {
                try {
                    bufferedReader = new BufferedReader(fileReader, 1024);
                    str = bufferedReader.readLine();
                } finally {
                    if (fileReader != null) {
                        try {
                            fileReader.close();
                        } catch (Throwable throwable) {
                        }
                    }
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable throwable) {
                        }
                    }
                }
            }
        } catch (Throwable throwable) {
        }
        return str;
    }


    private static String getMacAddressFromNetworkInterface() {
        try {
            Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) enumeration.nextElement();
                if ("wlan0".equals(networkInterface.getName()) || "eth0".equals(networkInterface.getName())) {
                    byte[] arrayOfByte = networkInterface.getHardwareAddress();
                    if (arrayOfByte == null || arrayOfByte.length == 0) {
                        return null;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    for (byte b1 : arrayOfByte) {
                        stringBuilder.append(String.format("%02X:", new Object[]{Byte.valueOf(b1)}));
                    }
                    if (stringBuilder.length() > 0) {
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    }
                    return stringBuilder.toString().toLowerCase(Locale.getDefault());
                }
            }
        } catch (Throwable throwable) {
        }
        return null;
    }


    private static String getSerial() {
        String str = "";
        if (Build.VERSION.SDK_INT >= 9 && Build.VERSION.SDK_INT < 26) {
            str = Build.SERIAL;
        } else if (Build.VERSION.SDK_INT >= 26) {
            try {
                Class clazz = Class.forName("android.os.Build");
                Method method = clazz.getMethod("getSerial", new Class[0]);
                str = (String) method.invoke(clazz, new Object[0]);
            } catch (Throwable throwable) {
            }
        }
        return str;
    }

    private static String getDeviceId(Context paramContext) {
        String str = "";
        TelephonyManager telephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            try {
                if (a("android.permission.READ_PHONE_STATE") != null) {
                    if (Build.VERSION.SDK_INT > 26) {
                        Class clazz = Class.forName("android.telephony.TelephonyManager");
                        Method method = clazz.getMethod("getImei", new Class[]{Integer.class});
                        str = (String) method.invoke(telephonyManager, new Object[]{method, Integer.valueOf(0)});
                        if (TextUtils.isEmpty(str)) {
                            method = clazz.getMethod("getMeid", new Class[]{Integer.class});
                            str = (String) method.invoke(telephonyManager, new Object[]{method, Integer.valueOf(0)});
                            if (TextUtils.isEmpty(str)) {
                                str = telephonyManager.getDeviceId();
                            }
                        }
                    } else {
                        str = telephonyManager.getDeviceId();
                    }
                }
            } catch (Throwable throwable) {
                str = "";
            }
        }
        return str;
    }

    private static String getMacAddressFromWifiManager(Context paramContext) {
        try {
            WifiManager wifiManager = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);
            if (a("android.permission.ACCESS_WIFI_STATE") != null) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                return wifiInfo.getMacAddress();
            }
            return "";
        } catch (Throwable throwable) {
            return "";
        }
    }
}