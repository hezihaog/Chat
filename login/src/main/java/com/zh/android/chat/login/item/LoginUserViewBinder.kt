package com.zh.android.chat.login.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImageToRound
import com.zh.android.base.ext.setGone
import com.zh.android.base.ext.setVisible
import com.zh.android.chat.login.R
import com.zh.android.chat.service.db.login.entity.LoginUserEntity
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/11/14
 * 登录账号信息条目
 */
class LoginUserViewBinder(
    private val itemClickCallback: (model: LoginUserEntity) -> Unit
) : ItemViewBinder<LoginUserEntity, LoginUserViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.login_user_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: LoginUserEntity) {
        item.run {
            holder.vAvatar.loadUrlImageToRound(avatar)
            holder.vNickname.text = nickname
            holder.vCurrentLoginTag.apply {
                if (loginFlag) {
                    setVisible()
                } else {
                    setGone()
                }
            }
            holder.itemView.click {
                itemClickCallback(item)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vAvatar: ImageView = view.findViewById(R.id.avatar)
        val vNickname: TextView = view.findViewById(R.id.nickname)
        val vCurrentLoginTag: View = view.findViewById(R.id.current_login_tag)
    }
}