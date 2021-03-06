package com.zh.android.chat.moment.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.model.MomentPublishMediaModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/09/21
 * 发布动态，图片条目
 */
class MomentPublishMediaViewBinder(
    /**
     * 点击删除回调
     */
    val clickDeleteCallback: (position: Int, item: MomentPublishMediaModel) -> Unit,
    /**
     * 条目点击回调
     */
    val clickItemCallback: (position: Int, item: MomentPublishMediaModel) -> Unit
) :
    ItemViewBinder<MomentPublishMediaModel, MomentPublishMediaViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.moment_publish_image_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MomentPublishMediaModel) {
        item.run {
            holder.vImage.loadUrlImage(url)
            holder.vDelete.click {
                clickDeleteCallback(getPosition(holder), item)
            }
            holder.itemView.click {
                clickItemCallback(getPosition(holder), item)
            }
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vImage: ImageView = view.findViewById(R.id.image)
        val vDelete: View = view.findViewById(R.id.delete)
    }
}