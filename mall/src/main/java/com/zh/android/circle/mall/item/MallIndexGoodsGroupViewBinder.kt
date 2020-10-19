package com.zh.android.circle.mall.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.model.MallGoodsModel
import com.zh.android.circle.mall.model.MallIndexGoodsGroupModel
import me.drakeet.multitype.ItemViewBinder
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/17
 * 商城首页-商品分组
 */
class MallIndexGoodsGroupViewBinder :
    ItemViewBinder<MallIndexGoodsGroupModel, MallIndexGoodsGroupViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.mall_index_goods_group_item_view,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MallIndexGoodsGroupModel) {
        val context = holder.itemView.context
        item.run {
            holder.vGroupName.text = groupName
            holder.vList.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = MultiTypeAdapter(Items(goods)).apply {
                    register(MallGoodsModel::class.java, InnerViewBinder())
                }
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vGroupName: TextView = view.findViewById(R.id.group_name)
        val vList: RecyclerView = view.findViewById(R.id.list)
    }

    inner class InnerViewBinder :
        ItemViewBinder<MallGoodsModel, InnerViewBinder.InnerViewHolder>() {
        override fun onCreateViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup
        ): InnerViewHolder {
            return InnerViewHolder(
                inflater.inflate(
                    R.layout.mall_index_goods_group_inner_item_view,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: InnerViewHolder, item: MallGoodsModel) {
            val context = holder.itemView.context
            item.run {
                holder.vImage.loadUrlImage(goodsCoverImg)
                holder.vName.text = goodsName
                holder.vPrice.text = context.resources.getString(
                    R.string.mall_rmb_price,
                    sellingPrice.toString()
                )
            }
        }

        inner class InnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val vImage: ImageView = view.findViewById(R.id.image)
            val vName: TextView = view.findViewById(R.id.name)
            val vPrice: TextView = view.findViewById(R.id.price)
        }
    }
}