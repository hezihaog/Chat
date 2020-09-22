package com.zh.android.chat.moment.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.base.ext.setGone
import com.zh.android.base.ext.setVisible
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.enums.MomentReplyType
import com.zh.android.chat.moment.model.MomentCommentModel
import com.zh.android.chat.moment.model.MomentCommentReplyModel
import me.drakeet.multitype.ItemViewBinder
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/09/19
 * 动态的评论条目
 */
class MomentCommentViewBinder(
    val clickItemCallback: (item: MomentCommentModel) -> Unit
) :
    ItemViewBinder<MomentCommentModel, MomentCommentViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.moment_comment_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MomentCommentModel) {
        val context = holder.itemView.context
        item.run {
            holder.avatar.loadUrlImage(ApiUrl.getFullImageUrl(userInfo.picNormal))
            holder.nickname.text = userInfo.nickname
            holder.createTime.text = createTime
            holder.content.text = content
            //该评论，没有回复
            if (replyList.isEmpty()) {
                holder.commentList.setGone()
            } else {
                holder.commentList.setVisible()
                //有回复才显示
                val items = Items().apply {
                    addAll(replyList)
                }
                //评论的回复列表
                holder.commentList.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = MultiTypeAdapter(items).apply {
                        //2种类型
                        register(MomentCommentReplyModel::class.java)
                            .to(CommentReplyViewBinder(), ReplyReplyViewBinder())
                            .withClassLinker { _, model ->
                                when (model.type) {
                                    MomentReplyType.COMMENT_REPLY.code -> {
                                        CommentReplyViewBinder::class.java
                                    }
                                    MomentReplyType.REPLY_REPLY.code -> {
                                        ReplyReplyViewBinder::class.java
                                    }
                                    else -> {
                                        CommentReplyViewBinder::class.java
                                    }
                                }
                            }
                    }
                }
            }
            holder.itemView.click {
                clickItemCallback(item)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val nickname: TextView = view.findViewById(R.id.nickname)
        val createTime: TextView = view.findViewById(R.id.create_time)
        val content: TextView = view.findViewById(R.id.content)
        val commentList: RecyclerView = view.findViewById(R.id.comment_list)
    }

    /**
     * 评论的回复
     */
    class CommentReplyViewBinder :
        ItemViewBinder<MomentCommentReplyModel, CommentReplyViewBinder.CommentReplyViewHolder>() {
        override fun onCreateViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup
        ): CommentReplyViewHolder {
            return CommentReplyViewHolder(
                inflater.inflate(
                    R.layout.moment_comment_replay_item_view,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(
            holder: CommentReplyViewHolder,
            item: MomentCommentReplyModel
        ) {
            val context = holder.itemView.context
            item.run {
                holder.nickname.text =
                    context.resources.getString(R.string.moment_comment_replay, userInfo.nickname)
                holder.content.text = content
            }
        }

        class CommentReplyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nickname: TextView = view.findViewById(R.id.nickname)
            val content: TextView = view.findViewById(R.id.content)
        }
    }

    /**
     * 回复的回复
     */
    class ReplyReplyViewBinder :
        ItemViewBinder<MomentCommentReplyModel, ReplyReplyViewBinder.ReplyReplyViewHolder>() {
        override fun onCreateViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup
        ): ReplyReplyViewHolder {
            return ReplyReplyViewHolder(
                inflater.inflate(
                    R.layout.moment_replay_replay_item_view,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ReplyReplyViewHolder, item: MomentCommentReplyModel) {
            val context = holder.itemView.context
            item.run {
                //发起回复的人
                holder.nickname.text =
                    context.resources.getString(R.string.moment_comment_replay, userInfo.nickname)
                //被回复的人
                holder.replyUserNickname.text =
                    context.resources.getString(
                        R.string.moment_comment_replay,
                        replyUserInfo.nickname
                    )
                holder.content.text = content
            }
        }

        class ReplyReplyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nickname: TextView = view.findViewById(R.id.nickname)
            val replyUserNickname: TextView = view.findViewById(R.id.reply_user_nickname)
            val content: TextView = view.findViewById(R.id.content)
        }
    }
}