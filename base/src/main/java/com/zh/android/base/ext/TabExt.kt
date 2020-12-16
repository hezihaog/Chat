package com.zh.android.base.ext

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * <b>Package:</b> com.linghit.base.ext <br>
 * <b>Create Date:</b> 2019-07-24  10:34 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> TabBar拓展 <br>
 */

/**
 * TanLayout绑定ViewPager2
 */
fun TabLayout.setupWithViewPager2(
    pager: ViewPager2,
    callback: (tab: TabLayout.Tab, position: Int) -> Unit
) {
    TabLayoutMediator(this, pager) { tab, position ->
        callback(tab, position)
    }.attach()
}