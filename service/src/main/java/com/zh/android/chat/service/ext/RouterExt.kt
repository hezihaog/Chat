package com.zh.android.chat.service.ext

import android.app.Activity
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl

/**
 * <b>Package:</b> com.linghit.base.ext <br>
 * <b>Create Date:</b> 2019-06-29  09:25 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> ARouter拓展 <br>
 */

/**
 * ARouter跳转Activity
 * @param requestCode startActivityForResult时使用的requestCode
 * @param callback 跳转回调
 */
@JvmOverloads
fun Postcard.startNavigation(
    activity: Activity,
    requestCode: Int = -1,
    callback: NavigationCallback? = null
) {
    navigation(activity, requestCode, object : NavigationCallback {
        override fun onFound(postcard: Postcard?) {
            //路由目标被发现时调用
            callback?.onFound(postcard)
        }

        override fun onArrival(postcard: Postcard?) {
            //路由到达时调用
            callback?.onArrival(postcard)
        }

        override fun onLost(postcard: Postcard?) {
            //路由被丢失时调用
            callback?.onLost(postcard)
        }

        override fun onInterrupt(postcard: Postcard?) {
            //未登录，拦截了，跳转到登录
            postcard?.run {
                if (extras.getBoolean(ARouterUrl.IS_LOGIN_INTERCEPTOR)) {
                    ARouter.getInstance()
                        .build(ARouterUrl.LOGIN_LOGIN)
                        .withTransition(0, 0)
                        .navigation(activity)
                }
            }
            //路由被拦截时调用
            callback?.onInterrupt(postcard)
        }
    })
}

/**
 * 拓展ARouter跳转方法，增加统一的回调处理
 * @return 这里返回Any?，是可空的原因是Kotlin返回不可空对象时生成的Java代码是会检查返回值是否为null
 * ARouter跳转Activity是返回Null的，跳转Fragment则返回Fragment实例，所以这里不能返回不可空的Any，否则会因为检查是否为null而抛出异常
 */
@JvmOverloads
fun Postcard.startNavigationFragment(
    activity: Activity,
    requestCode: Int = -1,
    callback: NavigationCallback? = null
): Fragment? {
    return this.navigation(activity, requestCode, object : NavigationCallback {
        override fun onFound(postcard: Postcard?) {
            //路由目标被发现时调用
            callback?.onFound(postcard)
        }

        override fun onArrival(postcard: Postcard?) {
            //路由到达时调用
            callback?.onArrival(postcard)
        }

        override fun onLost(postcard: Postcard?) {
            //路由被丢失时调用
            callback?.onLost(postcard)
        }

        override fun onInterrupt(postcard: Postcard?) {
            //未登录，拦截了，跳转到登录
            postcard?.run {
                if (extras.getBoolean(ARouterUrl.IS_LOGIN_INTERCEPTOR)) {
                    ARouter.getInstance()
                        .build(ARouterUrl.LOGIN_LOGIN)
                        .navigation(activity)
                }
            }
            //路由被拦截时调用
            callback?.onInterrupt(postcard)
        }
    }) as Fragment
}