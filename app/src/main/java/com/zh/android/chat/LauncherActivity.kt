package com.zh.android.chat

import android.view.View
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.youngfeng.snake.annotations.EnableDragToClose
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.service.module.home.HomeService
import com.zh.android.chat.service.module.login.LoginService

/**
 * @author wally
 * @date 2020/08/26
 */
@EnableDragToClose(value = false)
class LauncherActivity : BaseActivity() {
    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    @JvmField
    @Autowired(name = ARouterUrl.HOME_SERVICE)
    var mHomeService: HomeService? = null

    override fun onInflaterViewId(): Int {
        return -1
    }

    override fun onBindView(view: View?) {
        mLoginService?.let {
            //跳转到主页，会自动拦截没有登录的，跳转到登录界面
            mHomeService?.goHome(fragmentActivity, object : NavCallback() {
                override fun onArrival(postcard: Postcard?) {
                    finishSelf()
                }

                override fun onInterrupt(postcard: Postcard?) {
                    super.onInterrupt(postcard)
                    finishSelf()
                }
            })
        }
    }

    private fun finishSelf() {
        fragmentActivity.finish()
    }
}