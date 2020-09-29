package com.zh.android.chat.moment.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.model.MomentForwardRecordModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/09/19
 * 动态转发记录条目
 */
class MomentForwardRecordViewBinder :
    ItemViewBinder<MomentForwardRecordModel, MomentForwardRecordViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.moment_forward_record_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MomentForwardRecordModel) {
        item.run {
            holder.avatar.loadUrlImage(ApiUrl.getFullFileUrl(userInfo.avatar))
            holder.nickname.text = userInfo.nickname
            holder.createTime.text = createTime
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val nickname: TextView = view.findViewById(R.id.nickname)
        val createTime: TextView = view.findViewById(R.id.create_time)
    }
}