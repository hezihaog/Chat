package com.zh.android.chat.moment.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImageToRound
import com.zh.android.base.ext.setGone
import com.zh.android.base.ext.setVisible
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.model.MomentModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/09/19
 * 视频动态条目
 */
class MomentVideoItemViewBinder(
    val clickLikeCallback: (position: Int, item: MomentModel) -> Unit,
    val clickCommentCallback: (position: Int, item: MomentModel) -> Unit,
    val clickShareCallback: (position: Int, item: MomentModel) -> Unit,
    val clickItemCallback: (position: Int, item: MomentModel) -> Unit
) : ItemViewBinder<MomentModel, MomentVideoItemViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.moment_video_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MomentModel) {
        item.run {
            holder.avatar.loadUrlImageToRound(ApiUrl.getFullFileUrl(userInfo.picNormal))
            holder.nickname.text = userInfo.nickname
            holder.content.text = content
            //视频
            if (videos.isNotEmpty()) {
                holder.videoPlayer.run {
                    setVisible()
                    //配置视频控件
                    setUpLazy(ApiUrl.getFullFileUrl(videos[0]), true, null, null, content)
                    titleTextView.setGone()
                    backButton.setGone()
                    fullscreenButton.setGone()
                    //是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏
                    isAutoFullWithSize = true
                    //音频焦点冲突时是否释放
                    isReleaseWhenLossAudio = false
                    //全屏动画
                    isShowFullAnimation = true
                    //小屏时不触摸滑动
                    setIsTouchWiget(false)
                    //开始播放
                    startPlayLogic()
                    //播放完毕后，重播
                    setVideoAllCallBack(object : GSYSampleCallBack() {
                        override fun onAutoComplete(url: String?, vararg objects: Any?) {
                            super.onAutoComplete(url, *objects)
                            startPlayLogic()
                        }
                    })
                }
            } else {
                holder.videoPlayer.setGone()
            }
            holder.likeSymbol.apply {
                //切换点赞颜色
                if (liked) {
                    setImageResource(R.drawable.moment_love_red)
                } else {
                    setImageResource(R.drawable.moment_love_white)
                }
            }
            //点赞操作
            holder.likeLayout.click {
                clickLikeCallback(getPosition(holder), item)
            }
            //评论
            holder.commentLayout.click {
                clickCommentCallback(getPosition(holder), item)
            }
            //分享
            holder.shareLayout.click {
                clickShareCallback(getPosition(holder), item)
            }
            //点击条目
            holder.itemView.click {
                clickItemCallback(getPosition(holder), item)
            }
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val nickname: TextView = view.findViewById(R.id.nickname)
        val content: TextView = view.findViewById(R.id.content)
        val likeSymbol: ImageView = view.findViewById(R.id.like_symbol)
        val likeLayout: View = view.findViewById(R.id.like_layout)
        val commentLayout: View = view.findViewById(R.id.comment_layout)
        val shareLayout: View = view.findViewById(R.id.share_layout)
        val videoPlayer: StandardGSYVideoPlayer = view.findViewById(R.id.video_player)
    }
}