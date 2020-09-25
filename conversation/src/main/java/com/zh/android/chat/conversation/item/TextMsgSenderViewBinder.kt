package com.zh.android.chat.conversation.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.loadUrlImageToRound
import com.zh.android.chat.conversation.R
import com.zh.android.chat.service.module.conversation.model.ChatRecord
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/08/28
 * 文字消息-发送方条目
 */
class TextMsgSenderViewBinder : ItemViewBinder<ChatRecord, TextMsgSenderViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.conversation_text_msg_sender_item_view,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: ChatRecord) {
        item.run {
            holder.vAvatar.loadUrlImageToRound(ApiUrl.getFullImageUrl(fromUser.picNormal))
            holder.vContent.text = text?.content
            holder.vContent.setTextIsSelectable(true)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vAvatar: ImageView = view.findViewById(R.id.avatar)
        val vContent: TextView = view.findViewById(R.id.content)
    }
}