package com.zh.android.base.util.net

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import java.util.concurrent.CopyOnWriteArrayList


/**
 * <b>Package:</b> com.tongwei.smarttoilet.base.util.net <br>
 * <b>Create Date:</b> 2019-10-08  11:40 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 网络管理 <br>
 */
object NetManager {
    private val mNetListeners by lazy {
        CopyOnWriteArrayList<NetListener>()
    }

    /**
     * 开始监听
     */
    @JvmStatic
    fun startListener(context: Context) {
        context.applicationContext.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                context?.run {
                    if (isNetworkConnected(context)) {
                        mNetListeners.forEach {
                            it.onNetworkConnected()
                        }
                    } else {
                        mNetListeners.forEach {
                            it.onNetworkDisconnect()
                        }
                    }
                }
            }
        }, IntentFilter().apply {
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        })
    }

    interface NetListener {
        /**
         * 当连接上了网络时回调
         */
        fun onNetworkConnected()

        /**
         * 当断开网络连接时回调
         */
        fun onNetworkDisconnect()
    }

    /**
     * Block方式，增加网络监听
     */
    @JvmStatic
    fun addNetListener(connectedBlock: () -> Unit, disconnectBlock: () -> Unit) {
        addNetListener(object : NetListener {
            override fun onNetworkConnected() {
                connectedBlock()
            }

            override fun onNetworkDisconnect() {
                disconnectBlock()
            }
        })
    }

    /**
     * 增加网络监听
     */
    @JvmStatic
    fun addNetListener(listener: NetListener) {
        if (!mNetListeners.contains(listener)) {
            mNetListeners.add(listener)
        }
    }

    /**
     * 异常网络监听
     */
    @JvmStatic
    fun removeNetListener(listener: NetListener) {
        mNetListeners.remove(listener)
    }

    /**
     * 判断是否有网络连接
     */
    @JvmStatic
    fun isNetworkConnected(context: Context): Boolean {
        val manager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        if (networkInfo != null) {
            return networkInfo.isAvailable
        }
        return false
    }

    /**
     * 判断Wifi网络是否可用
     */
    @JvmStatic
    fun isWifiConnected(context: Context): Boolean {
        val manager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager
            .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (networkInfo != null) {
            return networkInfo.isAvailable
        }
        return false
    }
}