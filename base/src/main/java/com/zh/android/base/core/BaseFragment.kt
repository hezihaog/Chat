package com.zh.android.base.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.alibaba.android.arouter.launcher.ARouter


/**
 * Project: LingHitPlatform <br></br>
 * Create Date: 2018/2/21 <br></br>
 * @author: qy <br></br>
 * Address: qingyongai@gmail.com <br></br>
 * Description: baseFragment <br></br>
 */
abstract class BaseFragment : BaseSupportFragment(), LayoutCallback, LifecycleOwnerExt {
    val fragment: Fragment
        get() = this

    val lifecycleOwner: LifecycleOwnerExt
        get() = this

    val activityLifecycleOwner: LifecycleOwner
        get() = fragmentActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        onLayoutBefore()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutId = onInflaterViewId()
        if (layoutId == -1) {
            return null
        }
        val rootView = inflater.inflate(layoutId, container, false)
        onInflaterViewAfter(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isNeedLazy()) {
            onBindView(getView())
            setData()
        }
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        if (isNeedLazy()) {
            onBindView(view)
            setData()
        }
    }

    /**
     * 是否需要懒加载，默认为懒加载
     */
    protected open fun isNeedLazy(): Boolean {
        return true
    }

    override fun onInflaterViewAfter(view: View) {
    }

    override fun onLayoutBefore() {
    }

    override fun setData() {
    }

    override fun getFragmentActivity(): FragmentActivity {
        return activity!!
    }
}