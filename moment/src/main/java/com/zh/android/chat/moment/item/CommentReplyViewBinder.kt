package com.zh.android.chat.moment.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.base.ext.setGone
import com.zh.android.base.ext.setVisible
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.model.MomentCommentReplyModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/09/23
 * 评论的回复条目
 */
class CommentReplyViewBinder(
    /**
     * 点击评论按钮回调
     */
    private val clickCommentCallback: (item: MomentCommentReplyModel) -> Unit,
    /**
     * 点击删除按钮回调
     */
    private val clickDeleteCallback: (item: MomentCommentReplyModel) -> Unit
) :
    ItemViewBinder<MomentCommentReplyModel, CommentReplyViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.moment_comment_replay_item_view,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MomentCommentReplyModel) {
        val context = holder.itemView.context
        item.run {
            holder.avatar.loadUrlImage(userInfo.avatar)
            holder.nickname.text = userInfo.nickname
            holder.createTime.text = createTime
            holder.content.text = context.getString(R.string.moment_replay_to, content)
            //评论按钮
            holder.commentSymbol.click {
                clickCommentCallback(item)
            }
            //删除按钮
            holder.delete.run {
                if (me) {
                    setVisible()
                } else {
                    setGone()
                }
                click {
                    clickDeleteCallback(item)
                }
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val nickname: TextView = view.findViewById(R.id.nickname)
        val createTime: TextView = view.findViewById(R.id.create_time)
        val content: TextView = view.findViewById(R.id.content)
        val commentSymbol: View = view.findViewById(R.id.comment_symbol)
        val delete: View = view.findViewById(R.id.delete)
    }
}