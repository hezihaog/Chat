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
    /**
     * 是否显示评论按钮
     */
    private val isShowCommentBtn: Boolean = false,
    /**
     * 点击评论按钮回调
     */
    private val clickCommentCallback: ((item: MomentCommentModel) -> Unit)? = null,
    /**
     * 分割线高度
     */
    private val dividerHeight: Int,
    /**
     * 删除回调
     */
    private val clickDeleteCallback: (position: Int, item: MomentCommentModel) -> Unit,
    /**
     * 点击条目回调
     */
    private val clickItemCallback: ((item: MomentCommentModel) -> Unit)? = null
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
                            .to(InnerCommentReplyViewBinder {
                                clickItemCallback?.invoke(item)
                            },
                                InnerReplyReplyViewBinder {
                                    clickItemCallback?.invoke(item)
                                })
                            .withClassLinker { _, model ->
                                when (model.type) {
                                    MomentReplyType.COMMENT_REPLY.code -> {
                                        InnerCommentReplyViewBinder::class.java
                                    }
                                    MomentReplyType.REPLY_REPLY.code -> {
                                        InnerReplyReplyViewBinder::class.java
                                    }
                                    else -> {
                                        InnerCommentReplyViewBinder::class.java
                                    }
                                }
                            }
                    }
                }
            }
            //评论按钮
            holder.commentSymbol.run {
                if (isShowCommentBtn) {
                    setVisible()
                } else {
                    setGone()
                }
                click {
                    clickCommentCallback?.invoke(item)
                }
            }
            //删除评论
            holder.delete.run {
                if (me) {
                    setVisible()
                } else {
                    setGone()
                }
                click {
                    clickDeleteCallback(getPosition(holder), item)
                }
            }
            //分割线
            holder.divider.run {
                if (layoutParams.height != dividerHeight) {
                    layoutParams.height = dividerHeight
                    requestLayout()
                }
            }
            holder.itemView.click {
                clickItemCallback?.invoke(item)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val nickname: TextView = view.findViewById(R.id.nickname)
        val createTime: TextView = view.findViewById(R.id.create_time)
        val content: TextView = view.findViewById(R.id.content)
        val commentList: RecyclerView = view.findViewById(R.id.comment_list)
        val divider: View = view.findViewById(R.id.divider)
        val commentSymbol: View = view.findViewById(R.id.comment_symbol)
        val delete: View = view.findViewById(R.id.delete)
    }

    /**
     * 评论的回复
     */
    class InnerCommentReplyViewBinder(
        private val clickItemCallback: () -> Unit
    ) :
        ItemViewBinder<MomentCommentReplyModel, InnerCommentReplyViewBinder.CommentReplyViewHolder>() {
        override fun onCreateViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup
        ): CommentReplyViewHolder {
            return CommentReplyViewHolder(
                inflater.inflate(
                    R.layout.moment_inner_comment_replay_item_view,
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
                holder.itemView.click {
                    clickItemCallback()
                }
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
    class InnerReplyReplyViewBinder(
        private val clickItemCallback: () -> Unit
    ) :
        ItemViewBinder<MomentCommentReplyModel, InnerReplyReplyViewBinder.ReplyReplyViewHolder>() {
        override fun onCreateViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup
        ): ReplyReplyViewHolder {
            return ReplyReplyViewHolder(
                inflater.inflate(
                    R.layout.moment_inner_replay_replay_item_view,
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
                holder.itemView.click {
                    clickItemCallback()
                }
            }
        }

        class ReplyReplyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nickname: TextView = view.findViewById(R.id.nickname)
            val replyUserNickname: TextView = view.findViewById(R.id.reply_user_nickname)
            val content: TextView = view.findViewById(R.id.content)
        }
    }
}