package com.zh.android.chat.friend.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImageToRound
import com.zh.android.chat.friend.R
import com.zh.android.chat.service.module.mine.model.User
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/08/27
 * 好友条目
 */
class FriendViewBinder(
    private val itemClickCallback: (item: User) -> Unit
) : ItemViewBinder<User, FriendViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.friend_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: User) {
        item.run {
            holder.vAvatar.loadUrlImageToRound(ApiUrl.getFullFileUrl(avatar))
            holder.vName.text = nickname
            holder.itemView.click {
                itemClickCallback(this)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vAvatar: ImageView = view.findViewById(R.id.avatar)
        val vName: TextView = view.findViewById(R.id.name)
    }
}