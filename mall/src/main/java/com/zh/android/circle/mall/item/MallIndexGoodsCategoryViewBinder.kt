package com.zh.android.circle.mall.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.model.MallIndexGoodsCategoryModel
import me.drakeet.multitype.ItemViewBinder
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/17
 * 商城首页-商品分类条目
 */
class MallIndexGoodsCategoryViewBinder(
    private val onClickItemCallback: (position: Int, model: MallIndexGoodsCategoryModel.CategoryModel) -> Unit
) :
    ItemViewBinder<MallIndexGoodsCategoryModel, MallIndexGoodsCategoryViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.mall_index_goods_category_item_view,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MallIndexGoodsCategoryModel) {
        val context = holder.itemView.context
        item.run {
            holder.vList.apply {
                layoutManager = GridLayoutManager(context, 5)
                adapter = MultiTypeAdapter(Items(list)).apply {
                    register(
                        MallIndexGoodsCategoryModel.CategoryModel::class.java,
                        InnerViewBinder()
                    )
                }
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vList: RecyclerView = view.findViewById(R.id.list)
    }

    inner class InnerViewBinder :
        ItemViewBinder<MallIndexGoodsCategoryModel.CategoryModel, InnerViewBinder.InnerViewHolder>() {
        override fun onCreateViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup
        ): InnerViewHolder {
            return InnerViewHolder(
                inflater.inflate(
                    R.layout.mall_index_goods_category_inner_item_view,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(
            holder: InnerViewHolder,
            item: MallIndexGoodsCategoryModel.CategoryModel
        ) {
            item.run {
                holder.vImage.loadUrlImage(imgUrl)
                holder.vName.text = name
                holder.itemView.click {
                    onClickItemCallback(getPosition(holder), item)
                }
            }
        }

        inner class InnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val vImage: ImageView = view.findViewById(R.id.image)
            val vName: TextView = view.findViewById(R.id.name)
        }
    }
}