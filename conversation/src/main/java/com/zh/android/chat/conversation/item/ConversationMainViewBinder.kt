package com.zh.android.chat.conversation.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImageToRound
import com.zh.android.base.ext.longClick
import com.zh.android.base.ext.setTextWithDefault
import com.zh.android.chat.conversation.ChatMsgHelper
import com.zh.android.chat.conversation.R
import com.zh.android.chat.service.module.conversation.model.Conversation
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/08/28
 * 会话首页条目
 */
class ConversationMainViewBinder(
    private val longClickCallback: (position: Int, item: Conversation) -> Boolean,
    private val itemClickCallback: (item: Conversation) -> Unit
) :
    ItemViewBinder<Conversation, ConversationMainViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.conversation_main_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Conversation) {
        val context = holder.itemView.context
        item.run {
            val avatar = if (isMe) toUser.avatar else fromUser.avatar
            holder.vAvatar.loadUrlImageToRound(ApiUrl.getFullFileUrl(avatar))
            holder.vName.apply {
                if (isMe) {
                    setTextWithDefault(toUser.nickname)
                } else {
                    setTextWithDefault(fromUser.nickname)
                }
            }
            holder.vMsg.run {
                text = context.resources.getString(
                    R.string.conversation_say_tip,
                    fromUser.nickname, ChatMsgHelper.getChatText(context, item)
                )
            }
            holder.itemView.click {
                itemClickCallback(this)
            }
            holder.itemView.longClick {
                longClickCallback(getPosition(holder), item)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vAvatar: ImageView = view.findViewById(R.id.avatar)
        val vName: TextView = view.findViewById(R.id.name)
        val vMsg: TextView = view.findViewById(R.id.msg)
    }
}