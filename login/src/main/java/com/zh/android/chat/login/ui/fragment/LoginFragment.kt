package com.zh.android.chat.login.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.login.R
import com.zh.android.chat.login.http.LoginPresenter
import com.zh.android.chat.service.module.home.HomeService
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/08/26
 * 登录
 */
class LoginFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.HOME_SERVICE)
    var mHomeService: HomeService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vUsername: EditText by bindView(R.id.username)
    private val vPassword: EditText by bindView(R.id.password)
    private val vLogin: TextView by bindView(R.id.login)
    private val vRegister: TextView by bindView(R.id.register)

    private val mLoginPresenter by lazy {
        LoginPresenter()
    }

    private val mWaitController by lazy {
        WaitLoadingController(fragmentActivity, fragment)
    }

    companion object {
        fun newInstance(args: Bundle? = null): LoginFragment {
            val fragment = LoginFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.login_login_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.setTitle(getAppContext().getApplicationName())
        //登录
        vLogin.click {
            val username = vUsername.text.toString()
            if (username.isBlank()) {
                toast(R.string.login_username_input_hint)
                return@click
            }
            val password = vPassword.text.toString()
            if (password.isBlank()) {
                toast(R.string.login_pwd_input_hint)
                return@click
            }
            mLoginPresenter.login(username, password)
                .doOnSubscribeUi {
                    mWaitController.showWait()
                }
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({ model ->
                    mWaitController.hideWait()
                    if (handlerErrorCode(model)) {
                        goHomeFinishSelf()
                    }
                }, { error ->
                    error.printStackTrace()
                    mWaitController.hideWait()
                })
        }
        //注册
        vRegister.click {
            toast("注册")
        }
    }

    /**
     * 跳转到主页
     */
    private fun goHomeFinishSelf() {
        activity?.let { activity ->
            mHomeService?.goHome(activity, object : NavCallback() {
                override fun onArrival(postcard: Postcard?) {
                    activity.finish()
                }
            })
        }
    }
}