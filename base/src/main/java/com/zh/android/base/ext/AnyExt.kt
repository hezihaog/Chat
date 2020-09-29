package com.zh.android.base.ext

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.PowerManager
import android.os.Vibrator
import android.text.TextUtils
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.util.toast.ToastUtil
import com.zh.android.contextprovider.ContextProvider
import java.io.FileDescriptor
import java.io.InputStream

/**
 * <b>Package:</b> com.linghit.base.ext <br>
 * <b>Create Date:</b> 2019-06-29  09:29 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
fun Any?.isNull(): Boolean {
    return this == null
}

fun Any?.isNotNull(): Boolean {
    return !isNull()
}

/**
 * 获取App的全局Context
 */
fun getAppContext(): Context {
    return ContextProvider.get().context
}

/**
 * 获取Dialog的Activity
 */
fun Dialog.getActivity(): Activity? {
    return when (context) {
        is Activity -> context as Activity
        is ContextThemeWrapper -> {
            val wrapper = context as ContextThemeWrapper
            val baseContext = wrapper.baseContext
            if (baseContext is Activity) {
                baseContext
            } else {
                null
            }
        }
        else -> null
    }
}

//-------------------------------- Toast拓展 --------------------------------

fun Context.toast(str: Int) {
    ToastUtil.showMsg(this, str)
}

fun Context.toast(str: String) {
    ToastUtil.showMsg(this, str)
}

fun Context.toastLong(str: Int) {
    ToastUtil.showMsgLong(this, str)
}

fun Context.toastLong(str: String) {
    ToastUtil.showMsgLong(this, str)
}

//-------------------------------- Dialog专用 --------------------------------
fun Dialog.toast(str: Int) {
    getActivity()?.toast(str)
}

fun Dialog.toast(str: String) {
    getActivity()?.toast(str)
}

fun Dialog.toastLong(str: Int) {
    getActivity()?.toastLong(str)
}

fun Dialog.toastLong(str: String) {
    getActivity()?.toastLong(str)
}

//-------------------------------- Dialog专用 --------------------------------

/**
 * 不需要Activity的Toast
 */
fun toast(str: Int) {
    ContextProvider.get().context.toast(str)
}

fun toast(str: String) {
    ContextProvider.get().context.toast(str)
}

fun toastLong(str: Int) {
    ContextProvider.get().context.toastLong(str)
}

fun toastLong(str: String) {
    ContextProvider.get().context.toastLong(str)
}

//-------------------------------- Toast拓展 --------------------------------

fun runTaskOnUi(task: () -> Unit) {
    ContextProvider.get().post(task)
}

fun runTaskOnUiWithDelayed(task: () -> Unit, delayMillis: Long) {
    ContextProvider.get().postDelayed(task, delayMillis)
}

/**
 * 修复Rv的notifyItemRemoved，由于notifyItemRemoved后，position位置后的条目无法自动onBindView
 * 所以增加该拓展自动调用notifyItemRangeChanged，在删除后重新绑定position后的条目
 */
fun RecyclerView.Adapter<*>.fixNotifyItemRemoved(position: Int) {
    notifyItemRemoved(position)
    if (position < itemCount) {
        notifyItemRangeChanged(position, itemCount - position)
    }
}

/**
 * 获取布局填充服务
 */
val Context.layoutInflater: LayoutInflater
    get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

/**
 * 获取震动器服务
 */
val Context.vibrator: Vibrator
    get() = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

/**
 * 获取PowerManager
 */
val Context.powerManager: PowerManager?
    get() = getSystemService(Context.POWER_SERVICE) as? PowerManager

/**
 * 解决Android应用第一次安装成功后Home键切到后台再点击桌面图标应用重启的问题
 */
fun Activity.fixFirstLauncher() {
    if (!isTaskRoot) {
        val intent = intent
        val action = intent.action
        if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN == action) {
            finish()
            return
        }
    }
}

/**
 * 兼容获取String
 */
fun Context.getStringCompat(@StringRes resId: Int, vararg formatArgs: Any = arrayOf()): String {
    return if (formatArgs.isEmpty()) {
        resources.getString(resId)
    } else {
        resources.getString(resId, formatArgs)
    }
}

/**
 * Raw文件转Url
 */
fun Context.rawToUri(rawResId: Int): Uri {
    return Uri.parse("android.resource://$packageName/$rawResId")
}

/**
 * Raw文件转AssetFileDescriptor
 */
fun Context.rawToAssetFileDescriptor(rawResId: Int): AssetFileDescriptor {
    return resources.openRawResourceFd(rawResId)
}

/**
 * Raw文件转FileDescriptor
 */
fun Context.rawToFileDescriptor(rawResId: Int): FileDescriptor {
    return rawToAssetFileDescriptor(rawResId).fileDescriptor
}

/**
 * Raw文件转InputStream
 */
fun Context.rawToInputStream(rawResId: Int): InputStream {
    return resources.openRawResource(rawResId)
}

/**
 * 列出Asserts文件夹下的所有文件
 * @return asserts目录下的文件名列表
 */
fun Context.getAssertsFiles(): List<String> {
    return try {
        assets.list("")!!.toList<String>()
    } catch (e: Exception) {
        mutableListOf()
    }
}

/**
 * 判断字符串是否不为null，不为空字符串
 */
fun TextUtils.isNotEmpty(text: CharSequence): Boolean {
    return !TextUtils.isEmpty(text)
}

/**
 * 拓展Fragment获取applicationContext
 */
val Fragment.applicationContext
    get() = context?.applicationContext