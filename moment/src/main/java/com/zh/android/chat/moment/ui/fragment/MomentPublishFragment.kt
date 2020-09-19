package com.zh.android.chat.moment.ui.fragment

import android.os.Bundle
import android.view.View
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.moment.R
import com.zh.android.chat.service.module.base.UploadPresenter
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/09/20
 * 动态发布
 */
class MomentPublishFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)

    private val mUploadPresenter by lazy {
        UploadPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MomentPublishFragment {
            val fragment = MomentPublishFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.moment_publish_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.moment_publish)
        }
    }
}