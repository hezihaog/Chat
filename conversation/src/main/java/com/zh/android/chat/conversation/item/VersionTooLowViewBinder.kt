package com.zh.android.chat.conversation.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.chat.conversation.R
import com.zh.android.chat.service.module.conversation.model.ChatRecord
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/09/25
 */
class VersionTooLowViewBinder : ItemViewBinder<ChatRecord, VersionTooLowViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.conversation_chat_version_too_low_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: ChatRecord) {
        holder.vContent.setText(R.string.conversation_chat_version_too_low_tip)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vContent: TextView = view.findViewById(R.id.content)
    }
}