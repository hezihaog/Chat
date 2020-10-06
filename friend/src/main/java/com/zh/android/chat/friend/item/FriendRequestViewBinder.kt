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
import com.zh.android.chat.friend.model.FriendRequest
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/08/28
 * 好友申请记录条目
 */
class FriendRequestViewBinder(
    val clickIgnoreCallback: (position: Int, item: FriendRequest) -> Unit,
    val clickAllowCallback: (position: Int, item: FriendRequest) -> Unit
) :
    ItemViewBinder<FriendRequest, FriendRequestViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.friend_request_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: FriendRequest) {
        item.run {
            holder.vAvatar.loadUrlImageToRound(ApiUrl.getFullFileUrl(avatar))
            holder.vNickName.text = nickname
            //拒绝
            holder.vIgnore.click {
                val position = getPosition(holder)
                clickIgnoreCallback(position, item)
            }
            //允许
            holder.vAllow.click {
                val position = getPosition(holder)
                clickAllowCallback(position, item)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vAvatar: ImageView = view.findViewById(R.id.avatar)
        val vNickName: TextView = view.findViewById(R.id.nickname)
        val vIgnore: TextView = view.findViewById(R.id.ignore)
        val vAllow: TextView = view.findViewById(R.id.allow)
    }
}