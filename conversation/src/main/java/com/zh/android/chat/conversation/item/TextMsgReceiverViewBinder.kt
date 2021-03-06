package com.zh.android.chat.conversation.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImageToRound
import com.zh.android.base.ext.longClick
import com.zh.android.chat.conversation.R
import com.zh.android.chat.service.module.conversation.model.ChatRecord
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/08/28
 * 文字消息-接收方条目
 */
class TextMsgReceiverViewBinder(
    private val clickCallback: (position: Int, item: ChatRecord) -> Unit,
    private val longClickCallback: (position: Int, item: ChatRecord) -> Boolean
) :
    ItemViewBinder<ChatRecord, TextMsgReceiverViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.conversation_text_msg_receiver_item_view,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: ChatRecord) {
        item.run {
            holder.vAvatar.loadUrlImageToRound(fromUser.avatar)
            holder.vContent.text = text?.content
            holder.vContent.run {
                click {
                    clickCallback(getPosition(holder), item)
                }
                longClick {
                    longClickCallback(getPosition(holder), item)
                }
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vAvatar: ImageView = view.findViewById(R.id.avatar)
        val vContent: TextView = view.findViewById(R.id.content)
    }
}