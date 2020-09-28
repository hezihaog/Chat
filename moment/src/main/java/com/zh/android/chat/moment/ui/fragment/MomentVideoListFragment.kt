package com.zh.android.chat.moment.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.apkfuns.logutils.LogUtils
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.BroadcastRegistry
import com.zh.android.base.util.ShareUtil
import com.zh.android.base.util.StatusBarUtil
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.http.MomentPresenter
import com.zh.android.chat.moment.item.MomentVideoItemViewBinder
import com.zh.android.chat.moment.model.MomentModel
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.moment.MomentService
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import org.joor.Reflect

/**
 * @author wally
 * @date 2020/09/28
 * 动态视频列表
 */
class MomentVideoListFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.MOMENT_SERVICE)
    var mMomentService: MomentService? = null

    private val vFakeStatusBar: View by bindView(R.id.fake_status_bar)
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vPager: ViewPager2 by bindView(R.id.pager)

    private var mCurrentPage: Int = ApiUrl.FIRST_PAGE

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(MomentModel::class.java, MomentVideoItemViewBinder(
                { _, item ->
                    //取反状态
                    val isLike = !item.liked
                    likeOrRemoveLikeMoment(isLike, item.id)
                }, { _, item ->
                    mMomentService?.goMomentDetail(fragmentActivity, item.id)
                }, { _, item ->
                    ShareUtil.shareText(fragmentActivity, item.content)
                    //增加一条转发记录
                    forwardMoment(item.id)
                }, { _, item ->
                    mMomentService?.goMomentDetail(fragmentActivity, item.id)
                }
            ))
        }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //发布成功
        BroadcastRegistry(lifecycleOwner)
            .register(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    refresh()
                }
            }, AppConstant.Action.MOMENT_PUBLISH_SUCCESS)
        BroadcastRegistry(lifecycleOwner)
            .register(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    intent?.run {
                        val momentId = getStringExtra(AppConstant.Key.MOMENT_ID)
                        if (momentId.isNullOrBlank()) {
                            return@run
                        }
                        val models = mListItems.filterIsInstance<MomentModel>()
                            .filter {
                                it.id == momentId
                            }
                        if (models.isNotEmpty() && models.size == 1) {
                            val momentModel = models[0]
                            val index = mListItems.indexOf(momentModel)
                            if (index != -1) {
                                if (mListItems.remove(momentModel)) {
                                    mListAdapter.fixNotifyItemRemoved(index)
                                }
                            }
                        }
                    }
                }
            }, AppConstant.Action.MOMENT_DELETE_SUCCESS)
        //切换点赞
        BroadcastRegistry(fragment)
            .register(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    intent?.run {
                        val momentId = intent.getStringExtra(AppConstant.Key.MOMENT_ID) ?: ""
                        val isLike = intent.getBooleanExtra(AppConstant.Key.MOMENT_IS_LIKE, false)
                        val likeNum = intent.getIntExtra(AppConstant.Key.MOMENT_LIKE_NUM, 0)
                        //点赞切换
                        mListItems.filterIsInstance<MomentModel>()
                            .filter {
                                it.id == momentId
                            }
                            .map {
                                it.liked = isLike
                                it.likes = likeNum
                            }
                        mListAdapter.notifyDataSetChanged()
                    }
                }
            }, AppConstant.Action.MOMENT_LIKE_CHANGE)
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
                    toast("搜索")
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
            val innerRecyclerView = Reflect.on(this).field("mRecyclerView").get<RecyclerView>()
            innerRecyclerView.setItemViewCacheSize(-1)
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
     * 点赞或取消点赞，动态
     */
    private fun likeOrRemoveLikeMoment(isLike: Boolean, momentId: String) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        val observable = if (isLike) {
            mMomentPresenter.likeMoment(momentId, userId)
        } else {
            mMomentPresenter.removeLikeMoment(momentId, userId)
        }
        observable.ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    httpModel?.data?.let { response ->
                        //更新数据
                        mListItems
                            //只取一个动态类型
                            .filterIsInstance<MomentModel>()
                            //只修改自己那一个动态
                            .filter {
                                it.id == response.momentId
                            }
                            .forEach {
                                it.liked = response.liked
                                it.likes = response.likes
                            }
                    }
                    mListAdapter.notifyDataSetChanged()
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
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
                                addAll(resultList)
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

    /**
     * 转发动态
     * @param momentId 动态Id
     */
    private fun forwardMoment(momentId: String) {
        getLoginService()?.run {
            val userId = getUserId()
            mMomentPresenter.forwardMoment(momentId, userId)
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({ httpModel ->
                    if (handlerErrorCode(httpModel)) {
                        LogUtils.d("转发动态成功，momentId：${momentId}, userId：$userId")
                    }
                }, {
                    it.printStackTrace()
                })
        }
    }
}