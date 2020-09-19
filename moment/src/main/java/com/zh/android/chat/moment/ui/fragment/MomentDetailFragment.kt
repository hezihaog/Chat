package com.zh.android.chat.moment.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.linghit.base.util.argument.bindArgument
import com.lzy.ninegrid.ImageInfo
import com.lzy.ninegrid.NineGridView
import com.lzy.ninegrid.preview.NineGridViewClickAdapter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.ui.fragment.BaseFragmentStateAdapter
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.http.MomentPresenter
import com.zh.android.chat.moment.model.MomentModel
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/09/19
 */
class MomentDetailFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vHeaderView: View by bindView(R.id.header_view)
    private val vTabBar: TabLayout by bindView(R.id.tab_bar)
    private val vPager: ViewPager2 by bindView(R.id.view_page)

    private val vAvatar by lazy {
        vHeaderView.findViewById<ImageView>(R.id.avatar)
    }
    private val vNickname by lazy {
        vHeaderView.findViewById<TextView>(R.id.nickname)
    }
    private val vCreateTime by lazy {
        vHeaderView.findViewById<TextView>(R.id.create_time)
    }
    private val vContent by lazy {
        vHeaderView.findViewById<TextView>(R.id.content)
    }
    private val vNineGridView by lazy {
        vHeaderView.findViewById<NineGridView>(R.id.nine_grid_view)
    }

    private val mMomentId by bindArgument(AppConstant.Key.MOMENT_ID, "")

    private val mMomentPresenter by lazy {
        MomentPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MomentDetailFragment {
            val fragment = MomentDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.moment_detail_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.moment_detail)
        }
        vRefreshLayout.apply {
            setOnRefreshListener {
                refresh()
            }
            setEnableLoadMore(false)
        }
        setupTab()
    }

    private fun setupTab() {
        val tabTitles = mutableListOf(
            getString(R.string.moment_comment),
            getString(R.string.moment_like)
        )
        val tabItems = mutableListOf<BaseFragmentStateAdapter.TabInfo>().apply {
            add(
                BaseFragmentStateAdapter.TabInfo(
                    MomentCommentFragment::class.java.name,
                    arguments ?: Bundle()
                )
            )
            add(
                BaseFragmentStateAdapter.TabInfo(
                    MomentLikeFragment::class.java.name,
                    arguments ?: Bundle()
                )
            )
        }
        val pageAdapter =
            BaseFragmentStateAdapter(context!!, childFragmentManager, lifecycle, tabItems)
        vPager.apply {
            adapter = pageAdapter
        }
        vTabBar.setupWithViewPager2(vPager) { tab, position ->
            tab.text = tabTitles[position]
        }
    }

    override fun setData() {
        super.setData()
        refresh()
    }

    private fun refresh() {
        getMomentDetail()
    }

    /**
     * 获取动态详情
     */
    private fun getMomentDetail() {
        val userId = getLoginService()?.getUserId()
        mMomentPresenter.getMomentDetail(mMomentId, userId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                vRefreshLayout.finishRefresh()
                if (handlerErrorCode(httpModel)) {
                    renderHeaderView(httpModel.data)
                }
            }, {
                it.printStackTrace()
                showRequestError()
                vRefreshLayout.finishRefresh()
            })
    }

    /**
     * 渲染头部布局
     */
    private fun renderHeaderView(data: MomentModel?) {
        if (data == null) {
            vHeaderView.setGone()
        } else {
            vHeaderView.setVisible()
            data.run {
                vAvatar.loadUrlImage(ApiUrl.getFullImageUrl(userInfo.picNormal))
                vNickname.text = userInfo.nickname
                vCreateTime.text = createTime
                vContent.text = content
                if (pictures.isNotEmpty()) {
                    vNineGridView.setVisible()
                    //图片信息
                    val imageInfoList = mutableListOf<ImageInfo>().apply {
                        addAll(
                            pictures.map {
                                val info = ImageInfo()
                                info.bigImageUrl = ApiUrl.getFullImageUrl(it)
                                info.thumbnailUrl = ApiUrl.getFullImageUrl(it)
                                info
                            }
                        )
                    }
                    //适配器
                    vNineGridView.setAdapter(NineGridViewClickAdapter(context, imageInfoList))
                } else {
                    vNineGridView.setGone()
                }
            }
        }
    }
}