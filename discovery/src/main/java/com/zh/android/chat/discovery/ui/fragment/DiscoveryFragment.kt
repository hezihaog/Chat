package com.zh.android.chat.discovery.ui.fragment

import android.view.View
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.discovery.R
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/08/26
 * 发现首页
 */
class DiscoveryFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)

    override fun onInflaterViewId(): Int {
        return R.layout.base_refresh_layout_with_top_bar
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            setTitle(getString(R.string.discovery_module_name))
        }
    }
}