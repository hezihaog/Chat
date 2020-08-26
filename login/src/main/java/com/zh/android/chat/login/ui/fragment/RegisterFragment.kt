package com.zh.android.chat.login.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.login.R
import com.zh.android.chat.login.http.LoginPresenter
import com.zh.android.chat.service.AppConstant
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/08/26
 */
class RegisterFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vUsername: EditText by bindView(R.id.username)
    private val vPassword: EditText by bindView(R.id.password)
    private val vVerifyPassword: EditText by bindView(R.id.verify_password)
    private val vRegister: TextView by bindView(R.id.register)

    private val mLoginPresenter by lazy {
        LoginPresenter()
    }

    private val mWaitController by lazy {
        WaitLoadingController(fragmentActivity, fragment)
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): RegisterFragment {
            val fragment = RegisterFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.login_register_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            setTitle(R.string.login_register)
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
        }
        vRegister.click {
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
            val verifyPassword = vVerifyPassword.text.toString()
            if (verifyPassword.isBlank()) {
                toast(R.string.login_verify_pwd_input_hint)
                return@click
            }
            if (password != verifyPassword) {
                toast(R.string.login_verify_pwd_fail)
                return@click
            }
            mLoginPresenter.register(username, password)
                .doOnSubscribeUi {
                    mWaitController.hideWait()
                }
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({ model ->
                    mWaitController.hideWait()
                    if (handlerErrorCode(model)) {
                        toast(R.string.login_register_success)
                        fragmentActivity.setResult(Activity.RESULT_OK, Intent().apply {
                            putExtra(AppConstant.Key.USER_NAME, username)
                        })
                        fragmentActivity.finish()
                    }
                }, { error ->
                    error.printStackTrace()
                    mWaitController.hideWait()
                })
        }
    }
}