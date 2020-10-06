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
import com.zh.android.base.ext.setGone
import com.zh.android.base.ext.setVisible
import com.zh.android.chat.friend.R
import com.zh.android.chat.friend.enums.FriendRequestStatus
import com.zh.android.chat.friend.model.VicinityUserModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/10/06
 * 附近的人条目
 */
class VicinityUserViewBinder(
    /**
     * 点击好友申请回调
     */
    private val clickRequestCallback: (position: Int, item: VicinityUserModel) -> Unit
) :
    ItemViewBinder<VicinityUserModel, VicinityUserViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.friend_vicinity_user_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: VicinityUserModel) {
        val context = holder.itemView.context
        item.run {
            holder.vAvatar.loadUrlImageToRound(ApiUrl.getFullFileUrl(avatar))
            holder.vNickName.text = nickname
            holder.vSign.apply {
                if (sign.isNullOrBlank()) {
                    setGone()
                } else {
                    setVisible()
                    text = sign
                }
            }
            holder.vTip.apply {
                if (isSendRequest) {
                    setVisible()
                    //申请过，按状态显示
                    text = if (status == FriendRequestStatus.WAIT_OP.code) {
                        //申请了，等待对方通过
                        context.resources.getString(R.string.friend_is_send_request)
                    } else {
                        //对方操作了，还能在这个列表，只可能是拒绝了，已是好友的话，不会出现在这里
                        context.resources.getString(R.string.friend_reject_add)
                    }
                } else {
                    setGone()
                }
            }
            holder.vRequest.apply {
                if (isSendRequest) {
                    setGone()
                } else {
                    setVisible()
                }
                //未申请过，显示申请按钮
                text = context.resources.getString(R.string.friend_request2)
                //好友申请
                click {
                    val position = getPosition(holder)
                    clickRequestCallback(position, item)
                }
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vAvatar: ImageView = view.findViewById(R.id.avatar)
        val vNickName: TextView = view.findViewById(R.id.nickname)
        val vSign: TextView = view.findViewById(R.id.sign)
        val vRequest: TextView = view.findViewById(R.id.request)
        val vTip: TextView = view.findViewById(R.id.tip)
    }
}