package com.zh.android.base.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zh.android.base.ext.fixNotifyItemRemoved

/**
 * <b>Package:</b> com.tongwei.smarttoilet.base.ui.fragment <br>
 * <b>Create Date:</b> 2019-10-15  14:32 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
open class BaseFragmentStateAdapter(
    val context: Context,
    manager: FragmentManager,
    lifecycle: Lifecycle,
    private val tabInfoList: MutableList<TabInfo> = mutableListOf(),
    private val creator: FragmentCreator = DefaultFragmentCreator()
) :
    FragmentStateAdapter(manager, lifecycle) {

    /**
     * Tab模型
     */
    data class TabInfo(
        val fragmentName: String,
        val args: Bundle = Bundle()
    )

    /**
     * Fragment创建器
     */
    interface FragmentCreator {
        fun onCreateFragment(
            context: Context,
            fragmentName: String,
            args: Bundle
        ): Fragment
    }

    /**
     * 默认Fragment构造器
     */
    private class DefaultFragmentCreator : FragmentCreator {
        override fun onCreateFragment(
            context: Context,
            fragmentName: String,
            args: Bundle
        ): Fragment {
            return Fragment.instantiate(context, fragmentName, args)
        }
    }

    override fun getItemCount(): Int {
        return tabInfoList.size
    }

    override fun createFragment(position: Int): Fragment {
        val tabInfo = tabInfoList[position]
        return creator.onCreateFragment(context, tabInfo.fragmentName, tabInfo.args)
    }

    /**
     * 动态添加Fragment
     */
    fun addFragment(tabInfo: TabInfo) {
        tabInfoList.add(tabInfo)
        notifyDataSetChanged()
    }

    /**
     * 动态移除Fragment
     */
    fun removeFragment(position: Int) {
        tabInfoList.removeAt(position)
        fixNotifyItemRemoved(position)
    }
}