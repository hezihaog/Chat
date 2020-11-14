package com.zh.android.chat.setting.ui.fragment

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.BroadcastRegistry
import com.zh.android.base.util.UUIDUtil
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.widget.SwitchButton
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.module.login.LoginService
import com.zh.android.chat.service.module.setting.SettingService
import com.zh.android.chat.setting.R
import com.zh.android.imageloader.ImageLoader
import io.reactivex.Observable
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/10/16
 * 设置页面
 */
class SettingFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    @JvmField
    @Autowired(name = ARouterUrl.SETTING_SERVICE)
    var mSettingService: SettingService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vEnableSwipeBackSwitch: SwitchButton by bindView(R.id.enable_swipe_back_switch)
    private val vEnablePatternLockSwitch: SwitchButton by bindView(R.id.is_enable_pattern_lock)
    private val vLogout: TextView by bindView(R.id.logout)
    private val vClearCache: View by bindView(R.id.clear_cache)
    private val vAppCacheSize: TextView by bindView(R.id.app_cache_size)

    private val mWaitController by lazy {
        WaitLoadingController(fragmentActivity, fragment)
    }

    companion object {
        /**
         * 设置私密锁的请求码
         */
        private const val PATTERN_LOCK_REQUEST_CODE = 10086
        private val PATTERN_LOCK_ACTION_CODE = UUIDUtil.get32UUID()

        fun newInstance(args: Bundle? = Bundle()): SettingFragment {
            val fragment = SettingFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //接收验证私密锁结果
        BroadcastRegistry(lifecycleOwner)
            .register(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    intent?.run {
                        val id = getStringExtra(AppConstant.Key.PATTERN_LOCK_ACTION_CODE) ?: ""
                        if (id != PATTERN_LOCK_ACTION_CODE) {
                            return
                        }
                        val result =
                            getBooleanExtra(AppConstant.Key.PATTERN_LOCK_IS_VALIDATE, false)
                        if (result) {
                            //验证成功，关闭私密锁
                            mLoginService?.saveIsOpenPatternLock(false)
                            //切换按钮状态
                            vEnablePatternLockSwitch.isChecked = false
                        } else {
                            //验证失败，恢复为打开状态
                            vEnablePatternLockSwitch.isChecked = true
                        }
                    }
                }
            }, AppConstant.Action.PATTERN_LOCK_VALIDATE_RESULT)
    }

    override fun onResume() {
        super.onResume()
        //渲染缓存信息
        renderAppCacheSize()
    }

    override fun onInflaterViewId(): Int {
        return R.layout.setting_main_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.setting_module_name)
        }
        //切换开关侧滑返回
        vEnableSwipeBackSwitch.apply {
            isChecked = mSettingService?.isEnableSwipeBack() ?: true
            setOnCheckedChangeListener { _, isChecked ->
                mSettingService?.saveEnableSwipeBack(isChecked)
            }
        }
        //切换开关私密锁
        vEnablePatternLockSwitch.apply {
            isChecked = mLoginService?.isOpenPatternLock() ?: false
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    //开启私密锁
                    if (mLoginService?.getPatternLockStr().isNullOrBlank()) {
                        //没设置过，跳转到私密锁的设置页面，进行设置
                        mSettingService?.goPatternLockSetting(
                            fragmentActivity,
                            PATTERN_LOCK_REQUEST_CODE
                        )
                    } else {
                        //如果已经设置过私密锁，直接切换为开
                        mLoginService?.saveIsOpenPatternLock(true)
                    }
                } else {
                    //关闭私密锁，先验证，成功后，再进行关闭
                    mSettingService?.goPatternLockValidate(
                        fragmentActivity,
                        PATTERN_LOCK_ACTION_CODE
                    )
                }
            }
        }
        //清除缓存
        vClearCache.click {
            Observable.create<Boolean> { emitter ->
                //清除app缓存
                applicationContext?.clearAppCache()
                //清除图片缓存
                ImageLoader.get(fragmentActivity).loader.clearDiskCache()
                emitter.onNext(true)
            }.doOnSubscribeUi {
                mWaitController.showWait()
            }
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({
                    mWaitController.hideWait()
                    toast(R.string.base_operation_success)
                    //更新缓存大小
                    renderAppCacheSize()
                }, { error ->
                    error.printStackTrace()
                    mWaitController.hideWait()
                    toast(R.string.base_operation_fail)
                })
        }
        //退出登录
        vLogout.click {
            mLoginService?.logout(fragmentActivity)
            fragmentActivity.finish()
        }
    }

    /**
     * 渲染缓存信息
     */
    private fun renderAppCacheSize() {
        vAppCacheSize.text = applicationContext?.getAppCacheSize() ?: ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            //设置完毕私密锁
            PATTERN_LOCK_REQUEST_CODE -> {
                val isSetOk = resultCode == Activity.RESULT_OK
                //切换按钮状态
                vEnablePatternLockSwitch.isChecked = isSetOk
            }
        }
    }
}