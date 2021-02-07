package com.zh.android.chat.discovery

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.discovery.ui.fragment.DiscoveryFragment
import com.zh.android.chat.service.core.BaseModuleService
import com.zh.android.chat.service.ext.startNavigation
import com.zh.android.chat.service.module.discovery.DiscoveryService
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/08/26
 */
@Route(path = ARouterUrl.DISCOVERY_SERVICE, name = "发现模块服务")
class DiscoveryServiceImpl : BaseModuleService(), DiscoveryService {
    override fun init(context: Context?) {
    }

    override fun getDiscoveryFragment(): String {
        return DiscoveryFragment::class.java.name
    }

    override fun goQrCodeScan(activity: Activity): Observable<Boolean> {
        val fragmentActivity = activity as FragmentActivity
        return RxPermissions(fragmentActivity)
            .request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
            .flatMap {
                if (it) {
                    ARouter.getInstance()
                        .build(ARouterUrl.QR_CODE_SCAN)
                        .startNavigation(activity)
                }
                Observable.just(it)
            }
    }
}