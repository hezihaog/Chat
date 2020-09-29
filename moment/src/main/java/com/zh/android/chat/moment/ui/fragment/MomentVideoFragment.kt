package com.zh.android.chat.moment.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.apkfuns.logutils.LogUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.base.util.BroadcastRegistry
import com.zh.android.base.util.ShareUtil
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.http.MomentPresenter
import com.zh.android.chat.moment.model.MomentModel
import com.zh.android.chat.moment.ui.dialog.MomentCommentDialog
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.moment.MomentService
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/09/28
 * 具体的视频动态
 */
class MomentVideoFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.MOMENT_SERVICE)
    var mMomentService: MomentService? = null

    private val vAvatar: ImageView by bindView(R.id.avatar)
    private val vNickname: TextView by bindView(R.id.nickname)
    private val vContent: TextView by bindView(R.id.content)
    private val vLikeSymbol: ImageView by bindView(R.id.like_symbol)
    private val vLikeLayout: View by bindView(R.id.like_layout)
    private val vCommentLayout: View by bindView(R.id.comment_layout)
    private val vShareLayout: View by bindView(R.id.share_layout)
    private val vVideoPlayer: StandardGSYVideoPlayer by bindView(R.id.video_player)

    /**
     * 动态信息
     */
    private lateinit var mMomentInfo: MomentModel

    private val mMomentPresenter by lazy {
        MomentPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BroadcastRegistry(lifecycleOwner)
            .register(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val momentId = intent?.getStringExtra(AppConstant.Key.MOMENT_ID)
                    if (momentId.isNullOrBlank()) {
                        return
                    }
                    //开始播放视频
                    if (mMomentInfo.id == momentId) {
                        vVideoPlayer.startPlayLogic()
                    }
                }
            }, AppConstant.Action.MOMENT_PLAY_VIDEO)
    }

    override fun onLayoutBefore() {
        super.onLayoutBefore()
        mMomentInfo = arguments!!.getSerializable(AppConstant.Key.MOMENT_INFO) as MomentModel
    }

    override fun onInflaterViewId(): Int {
        return R.layout.moment_video_item_view
    }

    override fun onBindView(view: View?) {
        mMomentInfo.run {
            vAvatar.loadUrlImageToRound(ApiUrl.getFullFileUrl(userInfo.picNormal))
            vNickname.text = userInfo.nickname
            vContent.text = content
            //视频
            if (videos.isNotEmpty()) {
                vVideoPlayer.run {
                    setVisible()
                    //配置视频控件
                    val url = ApiUrl.getFullFileUrl(videos[0])
                    setUpLazy(url, true, null, null, content)
                    playTag = url
                    titleTextView.setGone()
                    backButton.setGone()
                    fullscreenButton.setGone()
                    //是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏
                    isAutoFullWithSize = true
                    //音频焦点冲突时是否释放
                    isReleaseWhenLossAudio = true
                    //全屏动画
                    isShowFullAnimation = true
                    //小屏时不触摸滑动
                    setIsTouchWiget(false)
                    //播放完毕后，重播
                    isLooping = true
                }
            } else {
                vVideoPlayer.setGone()
            }
            renderLike(liked)
            //点赞操作
            vLikeLayout.click {
                //取反状态
                val isLike = !liked
                likeOrRemoveLikeMoment(isLike, id)
            }
            //评论
            vCommentLayout.click {
                //显示评论弹窗
                MomentCommentDialog(fragmentActivity, fragment).apply {
                    setCancelable(true)
                    setCanceledOnTouchOutside(true)
                    loadData(mMomentInfo.id)
                    show()
                }
            }
            //分享
            vShareLayout.click {
                ShareUtil.shareText(fragmentActivity, content)
                //增加一条转发记录
                forwardMoment(id)
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
                        renderLike(response.liked)
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
     * 渲染点在
     */
    private fun renderLike(isLike: Boolean) {
        vLikeSymbol.apply {
            //切换点赞颜色
            if (isLike) {
                setImageResource(R.drawable.moment_love_red)
            } else {
                setImageResource(R.drawable.moment_love_white)
            }
        }
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
                        AppBroadcastManager.sendBroadcast(AppConstant.Action.MOMENT_FORWARD_SUCCESS)
                        LogUtils.d("转发动态成功，momentId：${momentId}, userId：$userId")
                    }
                }, {
                    it.printStackTrace()
                })
        }
    }
}