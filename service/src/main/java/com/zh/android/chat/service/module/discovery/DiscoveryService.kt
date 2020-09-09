package com.zh.android.chat.service.module.discovery

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/08/26
 */
interface DiscoveryService : IProvider {
    /**
     * 获取发现首页Fragment
     */
    fun getDiscoveryFragment(): String

    /**
     * 跳转到二维码扫描
     */
    fun goQrCodeScan(activity: Activity): Observable<Boolean>
}