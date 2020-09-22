package com.zh.android.chat.moment.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.apkfuns.logutils.LogUtils
import com.google.android.material.appbar.AppBarLayout
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
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.http.MomentPresenter
import com.zh.android.chat.moment.model.MomentModel
import com.zh.android.chat.moment.ui.widget.MomentInputBar
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/09/19
 */
class MomentDetailFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vAppBar: AppBarLayout by bindView(R.id.app_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vHeaderView: View by bindView(R.id.header_view)
    private val vTabBar: TabLayout by bindView(R.id.tab_bar)
    private val vPager: ViewPager2 by bindView(R.id.view_page)
    private val vMomentInputBar: MomentInputBar by bindView(R.id.moment_input_bar)
    private val vDelete: TextView by bindView(R.id.delete)

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
            addRightImageButton(R.drawable.base_more, R.id.topbar_item_more).click {
                toast("更多")
            }
        }
        vRefreshLayout.apply {
            setOnRefreshListener {
                refresh()
            }
            setEnableLoadMore(false)
        }
        vDelete.click {
            AlertDialog.Builder(fragmentActivity)
                .setMessage(R.string.moment_confirm_delete)
                .setPositiveButton(R.string.base_confirm) { _, _ ->
                    //删除动态
                    deleteMoment()
                }
                .setNegativeButton(R.string.base_cancel) { _, _ ->
                    LogUtils.d("取消删除动态")
                }
                .create()
                .show()
        }
        setupTab()
    }

    private fun setupTab() {
        val tabTitles = mutableListOf(
            getString(R.string.moment_comment),
            getString(R.string.moment_like),
            getString(R.string.moment_forward)
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
            add(
                BaseFragmentStateAdapter.TabInfo(
                    MomentForwardFragment::class.java.name,
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
                    val model = httpModel.data
                    renderHeaderView(model)
                    renderInputBar(model)
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
                //删除动态
                vDelete.run {
                    if (me) {
                        setVisible()
                    } else {
                        setGone()
                    }
                }
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

    /**
     * 渲染输入栏
     */
    private fun renderInputBar(data: MomentModel?) {
        vMomentInputBar.run {
            if (data == null) {
                setLike(false, "0")
                setCommentNum("0")
            } else {
                setLike(data.liked, data.likes.toString())
                setCommentNum(data.comments.toString())
                setOnActionCallback(object : MomentInputBar.OnActionCallback {
                    override fun onClickSendBefore(input: String?): Boolean {
                        return !input.isNullOrBlank()
                    }

                    override fun onClickSendAfter(inputText: String) {
                        //发送评论
                        addMomentComment(data.id, inputText)
                    }

                    override fun onClickLike(isLike: Boolean) {
                        //切换点赞
                        likeOrRemoveLikeMoment(isLike, data.id)
                    }

                    override fun onClickComment() {
                        //滚动评论列表
                        vAppBar.setExpanded(false, true)
                        //选中评论
                        vPager.setCurrentItem(0, false)
                    }
                })
            }
        }
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
                        //更新点赞状态
                        vMomentInputBar.setLike(
                            response.liked,
                            response.likes.toString()
                        )
                        AppBroadcastManager.sendBroadcast(
                            AppConstant.Action.MOMENT_LIKE_CHANGE,
                            Intent().apply {
                                putExtra(AppConstant.Key.MOMENT_ID, momentId)
                                putExtra(AppConstant.Key.MOMENT_IS_LIKE, response.liked)
                                putExtra(AppConstant.Key.MOMENT_LIKE_NUM, response.likes)
                            }
                        )
                    }
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }

    /**
     * 评论动态
     */
    private fun addMomentComment(
        momentId: String,
        content: String
    ) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mMomentPresenter.addMomentComment(momentId, userId, content)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    toast(R.string.moment_comment_success)
                    vMomentInputBar.setInputText("")
                    //刷新评论列表
                    AppBroadcastManager.sendBroadcast(AppConstant.Action.MOMENT_ADD_COMMENT_SUCCESS)
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }

    /**
     * 删除动态
     */
    private fun deleteMoment() {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mMomentPresenter.removeMoment(mMomentId, userId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    toast(R.string.moment_delete_success)
                    AppBroadcastManager.sendBroadcast(
                        AppConstant.Action.MOMENT_DELETE_SUCCESS,
                        Intent().apply {
                            putExtra(AppConstant.Key.MOMENT_ID, mMomentId)
                        }
                    )
                    fragmentActivity.finish()
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }
}