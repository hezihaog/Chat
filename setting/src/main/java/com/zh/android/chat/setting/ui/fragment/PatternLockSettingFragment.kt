package com.zh.android.chat.setting.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.github.ihsg.patternlocker.OnPatternChangeListener
import com.github.ihsg.patternlocker.PatternIndicatorView
import com.github.ihsg.patternlocker.PatternLockerView
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.ext.toast
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.setting.R
import com.zh.android.chat.setting.util.PatternHelper
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/11/10
 * 私密锁设置页面
 */
class PatternLockSettingFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vPatternLockIndicator: PatternIndicatorView by bindView(R.id.pattern_lock_indicator)
    private val vPatternLock: PatternLockerView by bindView(R.id.pattern_lock)

    private val mPatternLockHelper by lazy {
        PatternHelper()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): PatternLockSettingFragment {
            val fragment = PatternLockSettingFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.setting_pattern_lock_setting_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                setResultAndFinish(false)
            }
            setTitle(R.string.setting_pattern_lock_setting)
        }
        vPatternLock.apply {
            setOnPatternChangedListener(object : OnPatternChangeListener {
                override fun onStart(view: PatternLockerView) {
                }

                override fun onChange(view: PatternLockerView, hitIndexList: List<Int>) {
                }

                override fun onComplete(view: PatternLockerView, hitIndexList: List<Int>) {
                    //保存设置
                    mPatternLockHelper.validateForSetting(hitIndexList)
                    val isError = !mPatternLockHelper.isOk
                    vPatternLockIndicator.updateState(hitIndexList, isError)
                    mPatternLockHelper.message?.let {
                        toast(it)
                    }
                    //设置完毕
                    if (mPatternLockHelper.isFinish) {
                        setResultAndFinish(true)
                    }
                }

                override fun onClear(view: PatternLockerView) {
                    if (mPatternLockHelper.isFinish) {
                        setResultAndFinish(false)
                    }
                }
            })
        }
    }

    override fun onBackPressedSupport(): Boolean {
        //复写返回键，回传取消设置结果
        setResultAndFinish(false)
        return true
    }

    /**
     * 设置结果，并关闭页面
     * @param isOk 是否设置完毕
     */
    private fun setResultAndFinish(isOk: Boolean) {
        fragmentActivity.run {
            setResult(
                if (isOk) {
                    Activity.RESULT_OK
                } else {
                    Activity.RESULT_CANCELED
                }
            )
            finish()
        }
    }
}