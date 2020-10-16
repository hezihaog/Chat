package com.zh.android.circle.mall.ui.fragment

import android.os.Bundle
import android.view.View
import com.zh.android.base.core.BaseFragment
import com.zh.android.circle.mall.R

/**
 * @author wally
 * @date 2020/10/16
 * 商城首页
 */
class MallMainFragment : BaseFragment() {
    companion object {
        fun newInstance(args: Bundle? = Bundle()): MallMainFragment {
            val fragment = MallMainFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.mall_main_fragment
    }

    override fun onBindView(view: View?) {
    }
}