package com.zh.android.chat.home.ui.fragment

import android.os.Bundle
import android.view.View
import com.zh.android.base.core.BaseFragment
import com.zh.android.chat.home.R

/**
 * @author wally
 * @date 2020/08/26
 */
class HomeFragment : BaseFragment() {
    companion object {
        fun newInstance(args: Bundle? = Bundle()): HomeFragment {
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.home_home_fragment
    }

    override fun onBindView(view: View?) {
    }
}