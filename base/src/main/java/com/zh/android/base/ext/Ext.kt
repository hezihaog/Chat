package com.zh.android.base.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.webkit.WebView
import com.zh.android.base.util.system.SystemUtil

/**
 * <b>Package:</b> com.tongwei.bootrunsample <br>
 * <b>Create Date:</b> 2019-10-11  12:29 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */

/**
 * DSL，当前运行系统版本再，5.0以上时，才运行指定闭包
 */
fun supportsLollipop(block: () -> Unit) {
    supportsTarget(21, block)
}

/**
 * 7.0以上时才运行
 */
fun supportsN(block: () -> Unit) {
    supportsTarget(24, block)
}

/**
 * 7.1以上才运行
 */
fun supportsNMR1(block: () -> Unit) {
    supportsTarget(25, block)
}

/**
 * 9.0以上运行
 */
fun supportsP(block: () -> Unit) {
    supportsTarget(28, block)
}

/**
 * 运行在指定版本号
 */
fun supportsTarget(versionCode: Int, block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= versionCode) {
        block()
    }
}

/**
 * 获取当前App的VersionName
 */
fun Context.getAppVersionName(): String {
    return SystemUtil.getAppVersionName(this)
}

/**
 * 获取App名字
 */
fun Context.getApplicationName(): String {
    return SystemUtil.getApplicationName(this)
}

/**
 * 获取当前App包名
 */
fun Context.getAppPackageName(): String {
    try {
        val packageManager = packageManager
        val packageInfo = packageManager.getPackageInfo(
            packageName, 0
        )
        return packageInfo.packageName
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

/**
 * 处理Android9.0上的不同进程的WebView限制不同数据目录文件夹的问题
 */
@SuppressLint("NewApi")
fun Context.setupWebViewProcessDirectory() {
    supportsP {
        val appPackageName = getAppPackageName()
        val processName = Application.getProcessName()
        if (appPackageName != processName) {
            WebView.setDataDirectorySuffix("${processName}-tongwei")
        }
    }
}

/**
 * 使用本地浏览器打开Url
 */
fun String.localBrowserOpen(context: Context, openFail: ((msg:String) -> Unit)?=null) {
    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = Uri.parse(this@localBrowserOpen)
        //避免有些机型上会出现浏览器拉不起来的情况，例如华为P20
        addCategory(Intent.CATEGORY_BROWSABLE)
        if (context !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    //先判断是否有浏览器可以响应，有才进行跳转
    val manager = context.packageManager
    val intentActivities = manager.queryIntentActivities(intent, 0)
    if (intentActivities.size > 0) {
        context.startActivity(intent)
    } else {
        openFail?.invoke("您没有安装浏览器，请安装一个浏览器")
    }
}