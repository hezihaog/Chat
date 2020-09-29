package com.zh.android.chat.moment.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lzy.ninegrid.ImageInfo
import com.lzy.ninegrid.NineGridView
import com.lzy.ninegrid.preview.NineGridViewClickAdapter
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
 * 动态列表
 */
class MomentItemViewBinder(
    val clickLikeCallback: (position: Int, item: MomentModel) -> Unit,
    val clickCommentCallback: (position: Int, item: MomentModel) -> Unit,
    val clickShareCallback: (position: Int, item: MomentModel) -> Unit,
    val clickItemCallback: (position: Int, item: MomentModel) -> Unit
) : ItemViewBinder<MomentModel, MomentItemViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.moment_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MomentModel) {
        val context = holder.itemView.context
        item.run {
            holder.avatar.loadUrlImageToRound(ApiUrl.getFullFileUrl(userInfo.avatar))
            holder.nickname.text = userInfo.nickname
            holder.createTime.text = createTime
            holder.content.text = content
            if (pictures.isNotEmpty()) {
                holder.nineGridView.setVisible()
                //图片信息
                val imageInfoList = mutableListOf<ImageInfo>().apply {
                    addAll(
                        pictures.map {
                            val info = ImageInfo()
                            info.bigImageUrl = ApiUrl.getFullFileUrl(it)
                            info.thumbnailUrl = ApiUrl.getFullFileUrl(it)
                            info
                        }
                    )
                }
                //适配器
                holder.nineGridView.setAdapter(NineGridViewClickAdapter(context, imageInfoList))
            } else {
                holder.nineGridView.setGone()
            }
            //视频
            if (videos.isNotEmpty()) {
                holder.videoPlayer.run {
                    setVisible()
                    //配置视频控件
                    val url = ApiUrl.getFullFileUrl(videos[0])
                    setUp(url, true, null, null, content)
                    playTag = url
                    backButton.setGone()
                    fullscreenButton.click {
                        startWindowFullscreen(context, false, true)
                    }
                    //是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏
                    isAutoFullWithSize = true
                    //音频焦点冲突时是否释放
                    isReleaseWhenLossAudio = false
                    //全屏动画
                    isShowFullAnimation = true
                    //小屏时不触摸滑动
                    setIsTouchWiget(false)
                }
            } else {
                holder.videoPlayer.setGone()
            }
            //点赞数量
            holder.likeText.apply {
                text = likes.toString()
                //文字颜色
                if (liked) {
                    setTextColor(context.resources.getColor(R.color.base_blue))
                } else {
                    setTextColor(context.resources.getColor(R.color.base_gray4))
                }
            }
            holder.likeSymbol.apply {
                //切换点赞颜色
                if (liked) {
                    setImageResource(R.drawable.moment_liked)
                } else {
                    setImageResource(R.drawable.moment_like)
                }
            }
            //点赞操作
            holder.likeLayout.click {
                clickLikeCallback(getPosition(holder), item)
            }
            //评论
            holder.commentText.text = comments.toString()
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
        val createTime: TextView = view.findViewById(R.id.create_time)
        val content: TextView = view.findViewById(R.id.content)
        val nineGridView: NineGridView = view.findViewById(R.id.nine_grid_view)
        val likeSymbol: ImageView = view.findViewById(R.id.like_symbol)
        val likeText: TextView = view.findViewById(R.id.like_text)
        val likeLayout: View = view.findViewById(R.id.like_layout)
        val commentLayout: View = view.findViewById(R.id.comment_layout)
        val commentText: TextView = view.findViewById(R.id.comment_text)
        val shareLayout: View = view.findViewById(R.id.share_layout)
        val videoPlayer: StandardGSYVideoPlayer = view.findViewById(R.id.video_player)
    }
}