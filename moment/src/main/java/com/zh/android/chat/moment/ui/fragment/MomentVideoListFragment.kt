package com.zh.android.chat.moment.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.click
import com.zh.android.base.ext.handlerErrorCode
import com.zh.android.base.ext.ioToMain
import com.zh.android.base.ext.lifecycle
import com.zh.android.base.ui.fragment.BaseFragmentStateAdapter
import com.zh.android.base.util.StatusBarUtil
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.http.MomentPresenter
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import kotterknife.bindView
import org.joor.Reflect

/**
 * @author wally
 * @date 2020/09/28
 * 动态视频列表
 */
class MomentVideoListFragment : BaseFragment() {
    private val vFakeStatusBar: View by bindView(R.id.fake_status_bar)
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vPager: ViewPager2 by bindView(R.id.pager)

    private var mCurrentPage: Int = ApiUrl.FIRST_PAGE

    private val mListItems by lazy {
        mutableListOf<BaseFragmentStateAdapter.TabInfo>()
    }
    private val mListAdapter by lazy {
        BaseFragmentStateAdapter(
            fragmentActivity,
            childFragmentManager,
            lifecycle,
            mListItems
        )
    }

    private val mMomentPresenter by lazy {
        MomentPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MomentVideoListFragment {
            val fragment = MomentVideoListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }

    override fun onInflaterViewId(): Int {
        return R.layout.moment_video_list_fragment
    }

    override fun onBindView(view: View?) {
        vFakeStatusBar.run {
            //透明状态栏
            StatusBarUtil.setStatusBarTranslucent(fragmentActivity)
            StatusBarUtil.setStatusBarWhite(fragmentActivity)
            layoutParams.height = StatusBarUtil.getStatusBarHeight(fragmentActivity)
            requestLayout()
        }
        vTopBar.apply {
            //透明顶部栏
            setTopBarBackgroundColor(resources.getColor(R.color.base_transparent))
            //隐藏分割线
            setBackgroundDividerEnabled(false)
            //居中标题
            setTitleGravity(Gravity.CENTER)
            //白色字体
            setTitleTextColor(resources.getColor(R.color.base_white))
            addLeftBackImageButton().apply {
                setImageResource(R.drawable.base_topview_back_white)
                click {
                    fragmentActivity.finish()
                }
            }
            setTitle(R.string.moment_module_name)
            addRightImageButton(R.drawable.base_search_white, R.id.topbar_item_search)
                .click {
                }
        }
        vRefreshLayout.apply {
            setOnRefreshListener {
                refresh()
            }
            setEnableLoadMore(false)
        }
        vPager.apply {
            adapter = mListAdapter
            //上下滑动
            orientation = ViewPager2.ORIENTATION_VERTICAL
            //缓存数量，要求每次都调用bindView
            Reflect.on(this).field("mRecyclerView").get<RecyclerView>().apply {
                setItemViewCacheSize(-1)
            }
            //滚动监听
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    //到最后一个条目了，加载更多
                    if (position == (mListItems.size - 1)) {
                        loadMore()
                    }
                }
            })
        }
    }

    override fun setData() {
        super.setData()
        vRefreshLayout.autoRefresh()
    }

    override fun onBackPressedSupport(): Boolean {
        if (GSYVideoManager.backFromWindowFull(fragmentActivity)) {
            return true
        }
        return super.onBackPressedSupport()
    }

    private fun refresh() {
        mCurrentPage = ApiUrl.FIRST_PAGE
        getMomentVideoList(mCurrentPage)
    }

    private fun loadMore() {
        val nextPage = mCurrentPage + 1
        getMomentVideoList(nextPage)
    }

    /**
     * 获取动态视频列表
     */
    private fun getMomentVideoList(
        pageNum: Int
    ) {
        val userId = getLoginService()?.getUserId()
        val isFirstPage = pageNum == ApiUrl.FIRST_PAGE
        val pageSize = ApiUrl.PAGE_SIZE
        mMomentPresenter.getMomentVideoList(
            userId, pageNum, ApiUrl.PAGE_SIZE
        ).ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    httpModel.data?.list?.let { resultList ->
                        //数据为空
                        if (resultList.isEmpty()) {
                            if (isFirstPage) {
                                mListItems.clear()
                                mListAdapter.notifyDataSetChanged()
                                vRefreshLayout.finishRefresh()
                            } else {
                                vRefreshLayout.finishLoadMoreWithNoMoreData()
                            }
                        } else {
                            mListItems.apply {
                                if (isFirstPage) {
                                    mListItems.clear()
                                }
                                //添加条目
                                addAll(resultList.map {
                                    BaseFragmentStateAdapter.TabInfo(
                                        MomentVideoFragment::class.java.name,
                                        Bundle().apply {
                                            putSerializable(AppConstant.Key.MOMENT_INFO, it)
                                        }
                                    )
                                })
                            }
                            mListAdapter.notifyDataSetChanged()
                            //最后一页
                            if (resultList.size < pageSize) {
                                vRefreshLayout.finishRefresh()
                                vRefreshLayout.finishLoadMoreWithNoMoreData()
                            } else {
                                if (isFirstPage) {
                                    vRefreshLayout.finishRefresh()
                                } else {
                                    mCurrentPage++
                                    vRefreshLayout.finishLoadMore()
                                }
                            }
                        }
                    }
                    return@subscribe
                }
                //数据异常
                if (isFirstPage) {
                    if (mListItems.isEmpty()) {
                        vRefreshLayout.finishRefresh(false)
                    } else {
                        vRefreshLayout.finishRefresh(false)
                    }
                } else {
                    vRefreshLayout.finishLoadMore(false)
                }
            }, { error ->
                error.printStackTrace()
                if (isFirstPage) {
                    vRefreshLayout.finishRefresh(false)
                } else {
                    vRefreshLayout.finishLoadMore(false)
                }
            })
    }
}