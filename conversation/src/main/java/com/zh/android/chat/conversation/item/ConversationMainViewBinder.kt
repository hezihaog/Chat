package com.zh.android.chat.conversation.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImageToRound
import com.zh.android.chat.conversation.R
import com.zh.android.chat.conversation.enums.ReadStatus
import com.zh.android.chat.conversation.model.ChatRecord
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/08/28
 * 会话首页条目
 */
class ConversationMainViewBinder(
    private val itemClickCallback: (item: ChatRecord) -> Unit
) :
    ItemViewBinder<ChatRecord, ConversationMainViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.conversation_main_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: ChatRecord) {
        item.run {
            val context = holder.itemView.context
            holder.vAvatar.loadUrlImageToRound("")
            holder.vName.text = userId
            holder.vMsg.apply {
                text = message
                //未读为红色，已读则灰色
                if (ReadStatus.UNREAD.code == hasRead) {
                    setTextColor(context.resources.getColor(R.color.base_red))
                } else if (ReadStatus.READ.code == hasRead) {
                    setTextColor(context.resources.getColor(android.R.color.darker_gray))
                }
            }
            holder.itemView.click {
                itemClickCallback(this)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vAvatar: ImageView = view.findViewById(R.id.avatar)
        val vName: TextView = view.findViewById(R.id.name)
        val vMsg: TextView = view.findViewById(R.id.msg)
    }
}