package com.zh.android.chat.login.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.apkfuns.logutils.LogUtils
import com.blankj.utilcode.util.RegexUtils
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.rx.RxUtil
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.login.R
import com.zh.android.chat.login.http.LoginPresenter
import com.zh.android.chat.service.module.home.HomeService
import kotterknife.bindView
import java.util.concurrent.TimeUnit

/**
 * @author wally
 * @date 2020/09/09
 */
class LoginByPhoneFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.HOME_SERVICE)
    var mHomeService: HomeService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vPhone: EditText by bindView(R.id.phone)
    private val vAuthCode: EditText by bindView(R.id.auth_code)
    private val vGetAuthCode: TextView by bindView(R.id.get_auth_code)
    private val vLogin: TextView by bindView(R.id.login)

    /**
     * 是否可以请求获取验证码标记
     */
    private var isEnableRequestAuthCode = true

    private val mLoginPresenter by lazy {
        LoginPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): LoginByPhoneFragment {
            val fragment = LoginByPhoneFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.login_by_phone_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(getString(R.string.login_by_phone))
        }
        vGetAuthCode.click {
            getAuthCode()
        }
        vLogin.click {
            loginByAuthCode()
        }
    }

    /**
     * 获取验证码
     */
    private fun getAuthCode() {
        val phoneInput = vPhone.text.toString().trim()
        if (phoneInput.isBlank()) {
            toast(R.string.login_please_input_phone_num_tip)
            return
        }
        if (!RegexUtils.isMobileSimple(phoneInput)) {
            toast(R.string.login_phone_num_invalid_tip)
            return
        }
        mLoginPresenter.getAuthCode(phoneInput)
            .doOnNext {
                //开始倒计时
                startCountDown()
            }
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    if (httpModel.data.isNullOrBlank()) {
                        return@subscribe
                    }
                    val authCode = httpModel.data
                    LogUtils.d("获取到验证码：$authCode")
                }
            }, {
                it.printStackTrace()
            })
    }

    /**
     * 验证码登录
     */
    private fun loginByAuthCode() {
        val phoneInput = vPhone.text.toString().trim()
        if (phoneInput.isBlank()) {
            toast(R.string.login_please_input_phone_num_tip)
            return
        }
        val authCodeInput = vAuthCode.text.toString().trim()
        if (authCodeInput.isBlank()) {
            toast(R.string.login_please_input_auth_code_tip)
            return
        }
        mLoginPresenter.loginByAuthCode(phoneInput, authCodeInput)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    goHomeFinishSelf()
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }

    /**
     * 开始倒计时
     */
    private fun startCountDown() {
        if (!isEnableRequestAuthCode) {
            toast(R.string.login_is_send_auth_code_tip)
            return
        }
        val expireTime = ApiUrl.LOGIN_AUTH_CODE_EXPIRE_TIME
        RxUtil.countdown(0, 1, expireTime, TimeUnit.SECONDS)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({
                //到0时，变为可用
                val isEnable = it == 0
                renderCountDown(it, isEnable)
            }, {
                it.printStackTrace()
                resetAuthStatus()
            })
    }

    /**
     * 渲染倒计时
     * @param second 当前的秒数
     * @param isEnable 是否可用
     */
    private fun renderCountDown(second: Int, isEnable: Boolean) {
        if (isEnable) {
            //绿底，白字
            resetAuthStatus()
        } else {
            //透明底，灰字的按钮
            vGetAuthCode.apply {
                setBackgroundColor(fragmentActivity.resources.getColor(R.color.base_transparent))
                setTextColor(fragmentActivity.resources.getColor(R.color.base_gray))
                text = getString(R.string.login_count_down_str, second)
            }
        }
        isEnableRequestAuthCode = isEnable
    }

    /**
     * 重置状态，显示绿底，白字的按钮
     */
    private fun resetAuthStatus() {
        vGetAuthCode.apply {
            setBackgroundResource(R.drawable.base_btn_green)
            setTextColor(fragmentActivity.resources.getColor(R.color.base_white))
            text = getString(R.string.login_get_auth_code)
        }
        isEnableRequestAuthCode = true
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