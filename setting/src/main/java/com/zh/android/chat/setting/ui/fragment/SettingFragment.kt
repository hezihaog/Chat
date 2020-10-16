package com.zh.android.chat.setting.ui.fragment

import android.os.Bundle
import android.view.View
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.setting.R
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/10/16
 * 设置页面
 */
class SettingFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)

    companion object {
        fun newInstance(args: Bundle? = Bundle()): SettingFragment {
            val fragment = SettingFragment()
            fragment.arguments = args
            return fragment
        }
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
    }
}