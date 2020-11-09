package com.zh.android.chat.setting.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.widget.SwitchButton
import com.zh.android.base.widget.TopBar
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
    private val vLogout: TextView by bindView(R.id.logout)
    private val vClearCache: View by bindView(R.id.clear_cache)
    private val vAppCacheSize: TextView by bindView(R.id.app_cache_size)

    private val mWaitController by lazy {
        WaitLoadingController(fragmentActivity, fragment)
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): SettingFragment {
            val fragment = SettingFragment()
            fragment.arguments = args
            return fragment
        }
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
}