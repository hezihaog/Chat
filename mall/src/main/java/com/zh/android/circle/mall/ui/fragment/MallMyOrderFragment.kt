package com.zh.android.circle.mall.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.ext.setupWithViewPager2
import com.zh.android.base.ui.fragment.BaseFragmentStateAdapter
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.AppConstant
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.enums.OrderStatus
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/10/20
 * 我的订单
 */
class MallMyOrderFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vTabBar: TabLayout by bindView(R.id.tab_bar)
    private val vPager: ViewPager2 by bindView(R.id.pager)

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MallMyOrderFragment {
            val fragment = MallMyOrderFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.base_top_bar_with_tab_layout
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.mall_my_order)
        }
        setupTab()
    }

    private fun setupTab() {
        val tabInfos = mapOf(
            Pair(getString(R.string.mall_order_all), OrderStatus.DEFAULT),
            Pair(getString(R.string.mall_order_pre_pay), OrderStatus.ORDER_PRE_PAY),
            Pair(getString(R.string.mall_order_paid), OrderStatus.ORDER_PAID),
            Pair(getString(R.string.mall_order_order_packaged), OrderStatus.ORDER_PACKAGED),
            Pair(getString(R.string.mall_order_express), OrderStatus.ORDER_EXPRESS),
            Pair(getString(R.string.mall_order_success), OrderStatus.ORDER_SUCCESS)
        )
        val tabTitles = tabInfos.map {
            it.key
        }
        val tabItems = tabInfos.map {
            BaseFragmentStateAdapter.TabInfo(
                OrderListFragment::class.java.name,
                Bundle().apply {
                    putSerializable(
                        AppConstant.Key.MALL_ORDER_STATUS,
                        it.value
                    )
                }
            )
        }.toMutableList()
        val pageAdapter =
            BaseFragmentStateAdapter(context!!, childFragmentManager, lifecycle, tabItems)
        vPager.apply {
            adapter = pageAdapter
        }
        vTabBar.setupWithViewPager2(vPager) { tab, position ->
            tab.text = tabTitles[position]
        }
    }
}