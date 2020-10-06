package com.zh.android.chat.notice.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.chat.notice.R
import com.zh.android.chat.notice.enums.NoticeType
import com.zh.android.chat.notice.model.NoticeModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/10/06
 * 通知条目
 */
class NoticeItemViewBinder(
    val clickItemCallback: (position: Int, item: NoticeModel) -> Unit
) : ItemViewBinder<NoticeModel, NoticeItemViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.notice_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: NoticeModel) {
        val context = holder.itemView.context
        item.run {
            if (type == NoticeType.SYSTEM.code) {
                holder.vSymbol.setImageResource(R.drawable.notice_system_symbol)
            }
            holder.vTitle.run {
                if (read) {
                    setTextColor(context.resources.getColor(R.color.base_gray4))
                } else {
                    setTextColor(context.resources.getColor(R.color.base_black))
                }
                text = title
            }
            holder.vContent.text = content
            holder.vCreateTime.text = createTime
            holder.itemView.click {
                clickItemCallback(getPosition(holder), item)
            }
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vSymbol: ImageView = view.findViewById(R.id.symbol)
        val vTitle: TextView = view.findViewById(R.id.title)
        val vContent: TextView = view.findViewById(R.id.content)
        val vCreateTime: TextView = view.findViewById(R.id.create_time)
    }
}