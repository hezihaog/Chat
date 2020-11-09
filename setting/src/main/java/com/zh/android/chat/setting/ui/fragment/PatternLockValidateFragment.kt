package com.zh.android.chat.setting.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.github.ihsg.patternlocker.OnPatternChangeListener
import com.github.ihsg.patternlocker.PatternIndicatorView
import com.github.ihsg.patternlocker.PatternLockerView
import com.linghit.base.util.argument.bindArgument
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.ext.toast
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.setting.R
import com.zh.android.chat.setting.util.PatternHelper
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/11/09
 * 私密锁验证页面
 */
class PatternLockValidateFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vPatternLockIndicator: PatternIndicatorView by bindView(R.id.pattern_lock_indicator)
    private val vPatternLock: PatternLockerView by bindView(R.id.pattern_lock)

    private val mActionCode by bindArgument(AppConstant.Key.PATTERN_LOCK_ACTION_CODE, "")

    private val mPatternLockHelper by lazy {
        PatternHelper()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): PatternLockValidateFragment {
            val fragment = PatternLockValidateFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mActionCode.isBlank()) {
            sendResultAndFinish(false)
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.setting_pattern_lock_validate_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.setting_pattern_lock)
        }
        vPatternLock.apply {
            setOnPatternChangedListener(object : OnPatternChangeListener {
                override fun onStart(view: PatternLockerView) {
                }

                override fun onChange(view: PatternLockerView, hitIndexList: List<Int>) {
                }

                override fun onComplete(view: PatternLockerView, hitIndexList: List<Int>) {
                    //进行验证
                    mPatternLockHelper.validateForChecking(hitIndexList)
                    val isError = !mPatternLockHelper.isOk
                    vPatternLockIndicator.updateState(hitIndexList, isError)
                    mPatternLockHelper.message?.let {
                        toast(it)
                    }
                    //发送验证结果
                    if (!isError) {
                        //成功
                        sendResultAndFinish(true)
                    } else {
                        //失败
                        if (mPatternLockHelper.isFinish) {
                            sendResultAndFinish(false)
                        }
                    }
                }

                override fun onClear(view: PatternLockerView) {
                    if (mPatternLockHelper.isFinish) {
                        sendResultAndFinish(false)
                    }
                }
            })
        }
    }

    override fun onBackPressedSupport(): Boolean {
        //覆盖返回键，发送取消结果
        sendResultAndFinish(false)
        return true
    }

    /**
     * 发送结果，并关闭页面
     * @param isOk 是否成功
     */
    private fun sendResultAndFinish(isOk: Boolean) {
        AppBroadcastManager.sendBroadcast(
            AppConstant.Action.PATTERN_LOCK_VALIDATE_RESULT,
            Intent().apply {
                putExtra(AppConstant.Key.PATTERN_LOCK_ACTION_CODE, mActionCode)
                putExtra(AppConstant.Key.PATTERN_LOCK_IS_VALIDATE, isOk)
            })
        fragmentActivity.finish()
    }
}