package com.zh.android.circle.mall.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.model.MallGoodsCategoryModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/10/19
 * 商品分类-第一级条目
 */
class GoodsCategoryFirstLevelViewBinder(
    val clickItemCallback: (model: MallGoodsCategoryModel) -> Unit
) :
    ItemViewBinder<MallGoodsCategoryModel, GoodsCategoryFirstLevelViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.mall_goods_category_first_level_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MallGoodsCategoryModel) {
        val context = holder.itemView.context
        item.run {
            holder.vName.apply {
                text = categoryName
                //是否选中
                if (isSelect) {
                    setTextColor(context.resources.getColor(R.color.base_green3))
                    setBackgroundColor(context.resources.getColor(R.color.base_white))
                } else {
                    setTextColor(context.resources.getColor(R.color.base_black))
                    setBackgroundColor(context.resources.getColor(R.color.base_gray3))
                }
            }
            holder.itemView.click {
                clickItemCallback(item)
            }
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vName: TextView = view.findViewById(R.id.name)
    }
}