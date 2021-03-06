package com.zh.android.chat.moment.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.model.AddPublishMediaModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/09/21
 * 动态发布，默认资源条目
 */
class MomentAddPublishMediaViewBinder(
    val itemClickCallback: (model: AddPublishMediaModel) -> Unit
) :
    ItemViewBinder<AddPublishMediaModel, MomentAddPublishMediaViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.moment_publish_add_media_item_view,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: AddPublishMediaModel) {
        item.run {
            holder.itemView.click {
                itemClickCallback(item)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}