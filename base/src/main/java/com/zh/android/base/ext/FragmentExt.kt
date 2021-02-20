package com.zh.android.base.ext

import com.zh.android.base.core.BaseSupportActivity
import com.zh.android.base.core.BaseSupportFragment
import me.yokeyword.fragmentation.ISupportFragment

/**
 * @author wally
 * @date 2021/02/20
 * Fragment拓展
 */

/**
 * 加载根Fragment
 */
fun BaseSupportActivity.loadMainFragment(
    containerId: Int,
    fragment: ISupportFragment
) {
    if (findFragment(fragment::class.java) == null) {
        loadRootFragment(
            containerId,
            fragment
        )
    }
}

/**
 * 加载根Fragment
 */
fun BaseSupportFragment.loadMainFragment(
    containerId: Int,
    fragment: ISupportFragment
) {
    if (findFragment(fragment::class.java) == null) {
        loadRootFragment(
            containerId,
            fragment
        )
    }
}