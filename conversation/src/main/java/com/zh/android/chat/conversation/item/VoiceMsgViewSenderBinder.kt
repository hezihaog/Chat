package com.zh.android.chat.conversation.item

import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImageToRound
import com.zh.android.chat.conversation.R
import com.zh.android.chat.service.module.conversation.model.ChatRecord
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/09/26
 * 语音消息-发送方条目
 */
class VoiceMsgViewSenderBinder(
    private val clickVoiceCallback: (item: ChatRecord) -> Unit
) :
    ItemViewBinder<ChatRecord, VoiceMsgViewSenderBinder.ViewHolder>() {
    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.conversation_voice_msg_sender_item_view,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: ChatRecord) {
        val context = holder.itemView.context
        item.run {
            holder.vAvatar.loadUrlImageToRound(fromUser.avatar)
            holder.vTime.text = context.resources.getString(
                R.string.conversation_voice_time,
                voice?.mediaTime ?: 0
            )
            holder.vVoice.click {
                clickVoiceCallback(item)
            }
            updateVoiceSymbol(holder, isPlayingVoice)
        }
    }

    private fun updateVoiceSymbol(holder: ViewHolder, isPlaying: Boolean) {
        holder.vVoiceSymbol.run {
            val animationDrawable = background as AnimationDrawable
            if (isPlaying) {
                animationDrawable.start()
            } else {
                animationDrawable.stop()
                background = null
                setBackgroundResource(R.drawable.conversation_audio_animation_sender_list)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vAvatar: ImageView = view.findViewById(R.id.avatar)
        val vVoice: LinearLayout = view.findViewById(R.id.voice)
        val vTime: TextView = view.findViewById(R.id.time)
        val vVoiceSymbol: ImageView = view.findViewById(R.id.voice_symbol)
    }
}