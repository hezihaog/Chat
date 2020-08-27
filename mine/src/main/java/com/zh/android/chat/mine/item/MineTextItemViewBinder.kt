package com.zh.android.chat.mine.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.widget.iconfont.IconFontTextView
import com.zh.android.chat.mine.R
import com.zh.android.chat.mine.model.MineTextItemModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/08/27
 * 我的页面-文字型条目
 */
class MineTextItemViewBinder(
    private val itemClickCallback: (position: Int, item: MineTextItemModel) -> Unit
) :
    ItemViewBinder<MineTextItemModel, MineTextItemViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.mine_text_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MineTextItemModel) {
        item.run {
            holder.vItemName.text = itemName
            holder.vText.text = text
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
        val vText: TextView = view.findViewById(R.id.text)
        val vArrow: IconFontTextView = view.findViewById(R.id.arrow)
    }
}