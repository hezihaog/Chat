package com.zh.android.circle.mall.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.model.MallGoodsCategoryModel
import me.drakeet.multitype.ItemViewBinder
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/19
 * 商品分类-第二级分类
 */
class GoodsCategorySecondViewBinder(
    private val clickItemCallback: (model: MallGoodsCategoryModel) -> Unit
) :
    ItemViewBinder<MallGoodsCategoryModel, GoodsCategorySecondViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.mall_goods_category_second_level_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MallGoodsCategoryModel) {
        val context = holder.itemView.context
        item.run {
            holder.vName.text = categoryName
            holder.vChildList.apply {
                val items = Items(childList)
                layoutManager = GridLayoutManager(context, 3)
                adapter = MultiTypeAdapter(items).apply {
                    register(MallGoodsCategoryModel::class.java, ThreeLevelViewBinder())
                }
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vName: TextView = view.findViewById(R.id.name)
        val vChildList: RecyclerView = view.findViewById(R.id.child_list)
    }

    /**
     * 第三级分类
     */
    inner class ThreeLevelViewBinder :
        ItemViewBinder<MallGoodsCategoryModel, ThreeLevelViewBinder.ThreeLevelViewHolder>() {
        override fun onCreateViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup
        ): ThreeLevelViewHolder {
            return ThreeLevelViewHolder(
                inflater.inflate(
                    R.layout.mall_goods_category_three_level_item,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ThreeLevelViewHolder, item: MallGoodsCategoryModel) {
            item.run {
                holder.vImage.setImageResource(R.drawable.mall_goods_category_symbol)
                holder.vName.text = categoryName
                holder.itemView.click {
                    clickItemCallback(item)
                }
            }
        }

        inner class ThreeLevelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val vImage: ImageView = view.findViewById(R.id.image)
            val vName: TextView = view.findViewById(R.id.name)
        }
    }
}