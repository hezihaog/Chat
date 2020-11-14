package com.zh.android.chat.setting

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.util.BroadcastRegistry
import com.zh.android.base.util.UUIDUtil
import com.zh.android.base.util.activity.ActivityProvider
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.ext.getSettingService

/**
 * @author wally
 * @date 2020/11/09
 * 私密锁跳转拦截器
 */
@Interceptor(priority = 2)
class PatternLockInterceptor : IInterceptor {
    override fun init(context: Context?) {
    }

    override fun process(postcard: Postcard?, callback: InterceptorCallback?) {
        if (postcard == null || callback == null) {
            return
        }
        val activity = ActivityProvider.get().currentActivity as BaseActivity
        //判断跳转的页面是否需要验证私密锁
        val isNeedPatternLock = postcard.extra == AppConstant.Flag.IS_NEED_PATTERN_LOCK
        //是否开启私密锁
        val isOpenPatternLock = getLoginService()?.isOpenPatternLock() ?: false
        if (isNeedPatternLock && isOpenPatternLock) {
            val actionCode = UUIDUtil.get32UUID()
            //注册广播，用于回调结果
            BroadcastRegistry(activity.lifecycleOwner).register(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    intent?.run {
                        val id = getStringExtra(AppConstant.Key.PATTERN_LOCK_ACTION_CODE) ?: ""
                        if (id != actionCode) {
                            return
                        }
                        val result =
                            getBooleanExtra(AppConstant.Key.PATTERN_LOCK_IS_VALIDATE, false)
                        if (result) {
                            //验证成功，放行
                            callback.onContinue(postcard)
                        } else {
                            //验证失败，拦截
                            callback.onInterrupt(null)
                        }
                    }
                }
            }, AppConstant.Action.PATTERN_LOCK_VALIDATE_RESULT)
            //跳转去私密锁页面，如果验证成功，则继续跳转，否则进行拦截
            getSettingService()?.goPatternLockValidate(activity, actionCode)
        } else {
            //不需要，放行
            callback.onContinue(postcard)
        }
    }
}