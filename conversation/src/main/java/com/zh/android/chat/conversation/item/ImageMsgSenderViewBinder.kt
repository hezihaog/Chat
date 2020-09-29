package com.zh.android.chat.conversation.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.base.ext.loadUrlImageToRound
import com.zh.android.base.ext.longClick
import com.zh.android.chat.conversation.R
import com.zh.android.chat.service.module.conversation.model.ChatRecord
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/08/28
 * 图片消息-发送方条目
 */
class ImageMsgSenderViewBinder(
    private val longClickCallback: (position: Int, item: ChatRecord) -> Boolean,
    /**
     * 点击图片回调
     */
    private val clickImageCallback: (position: Int, item: ChatRecord) -> Unit
) : ItemViewBinder<ChatRecord, ImageMsgSenderViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.conversation_image_msg_sender_item_view,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: ChatRecord) {
        item.run {
            holder.vAvatar.loadUrlImageToRound(
                ApiUrl.getFullFileUrl(fromUser.avatar)
            )
            holder.vImage.loadUrlImage(
                ApiUrl.getFullFileUrl(
                    image?.image
                ),
                R.drawable.base_def_img_rect
            )
            holder.vImage.click {
                clickImageCallback(getPosition(holder), item)
            }
            holder.vImage.longClick {
                longClickCallback(getPosition(holder), item)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vAvatar: ImageView = view.findViewById(R.id.avatar)
        val vImage: ImageView = view.findViewById(R.id.image)
    }
}