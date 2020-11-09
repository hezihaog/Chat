package com.zh.android.base.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.math.BigDecimal;

/**
 * <b>Package:</b> com.tongwei.common.util <br>
 * <b>Create Date:</b> 2019-10-10  09:16 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 缓存清理 <br>
 */
public class DataCleanManager {
    public static String getDataPath(Context context) {
        return context.getFilesDir().getParent();
    }

    /**
     * 删除app_webview文件夹
     */
    public static void cleanWebCache(Context context) {
        String path = getDataPath(context) + "/app_webview";
        deleteFolderFile(path, true);
    }

    /**
     * * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
     */
    public static void cleanInternalCache(Context context) {
        deleteFolderFile(context.getCacheDir().getPath(), true);
    }

    /**
     * * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * *
     */
    public static void cleanDatabases(Context context) {
        String path = getDataPath(context) + "/databases";
        deleteFolderFile(path, true);
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
     */
    public static void cleanSharedPreference(Context context) {
        String path = getDataPath(context) + "/shared_prefs";
        deleteFolderFile(path, true);
    }

    /**
     * * 按名字清除本应用数据库 * *
     */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * * 清除/data/data/com.xxx.xxx/files下的内容 * *
     */
    public static void cleanFiles(Context context) {
        deleteFolderFile(context.getFilesDir().getPath(), true);
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = context.getExternalCacheDir();
            if (null != file && !isEmpty(file.getPath())) {
                deleteFolderFile(file.getPath(), true);
            }
        }
    }

    /**
     * * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * *
     */
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    /**
     * * 清除本应用所有的数据 * *
     *
     * @param context
     */
    public static void cleanApplicationData(Context context) {
        //cleanSharedPreference(context);
        cleanInternalCache(context);
        cleanExternalCache(context);
        //cleanDatabases(context);
        //cleanFiles(context);
        cleanWebCache(context);
        File dir = new File(context.getExternalFilesDir(null), "apk");
        File dir2 = new File(context.getExternalFilesDir(null), "lapkl");
        cleanCustomCache(dir.getPath());
        cleanCustomCache(dir2.getPath());
    }

    public static String getCacheSize(Context context) throws Exception {
        long in = getFolderSize(context.getCacheDir());
        long ex = 0;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ex = getFolderSize(context.getExternalCacheDir());
        }
        long fi = getFolderSize(context.getFilesDir());
        String path = getDataPath(context) + "/app_webview";
        long web = getFolderSize(new File(path));
        return getFormatSize((in + ex + fi + web));
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File value : fileList) {
                // 如果下面还有文件
                if (value.isDirectory()) {
                    size = size + getFolderSize(value);
                } else {
                    size = size + value.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                //如果下面还有文件
                if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    //如果是文件，删除
                    if (!file.isDirectory()) {
                        file.delete();
                    } else {// 目录
                        //目录下没有文件或者目录，删除
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化单位
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0 || "NULL".equals(str) || "null".equals(str);
    }
}