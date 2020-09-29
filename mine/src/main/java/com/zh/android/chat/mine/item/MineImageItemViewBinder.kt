package com.zh.android.chat.mine.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.base.ext.loadUrlImageToRound
import com.zh.android.base.widget.iconfont.IconFontTextView
import com.zh.android.chat.mine.R
import com.zh.android.chat.mine.model.MineImageItemModel
import de.hdodenhof.circleimageview.CircleImageView
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/08/27
 * 我的页面-图片型条目
 */
class MineImageItemViewBinder(
    private val itemClickCallback: (position: Int, item: MineImageItemModel) -> Unit
) :
    ItemViewBinder<MineImageItemModel, MineImageItemViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.mine_image_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MineImageItemModel) {
        val context = holder.itemView.context
        item.run {
            holder.vItemName.text = itemName
            holder.vImage.apply {
                //头像图片，才开启绘制圆角
                isDisableCircularTransformation = !isCircleImage
                if (isCircleImage) {
                    loadUrlImageToRound(imageUrl, defaultImageResId)
                } else {
                    loadUrlImage(imageUrl, defaultImageResId)
                }
            }
            holder.vArrow.run {
                //是否显示箭头
                visibility = if (isCanClick) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
            holder.itemView.click {
                if (isCanClick) {
                    val position = getPosition(holder)
                    itemClickCallback.invoke(position, item)
                }
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vItemName: TextView = view.findViewById(R.id.item_name)
        val vImage: CircleImageView = view.findViewById(R.id.image)
        val vArrow: IconFontTextView = view.findViewById(R.id.arrow)
    }
}