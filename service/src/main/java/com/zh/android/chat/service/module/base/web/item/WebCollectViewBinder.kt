package com.zh.android.chat.service.module.base.web.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.chat.service.R
import com.zh.android.chat.service.module.base.web.model.WebCollectModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2021/02/18
 */
class WebCollectViewBinder(
    private val onItemClickBlock: (position: Int, model: WebCollectModel) -> Unit,
    private val onItemLongClickBlock: (position: Int, model: WebCollectModel) -> Boolean
) : ItemViewBinder<WebCollectModel, WebCollectViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.service_web_collect_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: WebCollectModel) {
        item.run {
            holder.title.text = title
            holder.url.text = url
            holder.itemView.click {
                onItemClickBlock(getPosition(holder), item)
            }
            holder.itemView.setOnLongClickListener {
                onItemLongClickBlock(getPosition(holder), item)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val url: TextView = view.findViewById(R.id.url)
    }
}