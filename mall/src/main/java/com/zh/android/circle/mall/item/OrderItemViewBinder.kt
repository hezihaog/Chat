package com.zh.android.circle.mall.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.model.OrderItemModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/10/20
 * 订单项条目
 */
class OrderItemViewBinder(
    private val clickItemCallback: (model: OrderItemModel) -> Unit
) :
    ItemViewBinder<OrderItemModel, OrderItemViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.mall_order_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: OrderItemModel) {
        val context = holder.itemView.context
        item.run {
            holder.vImage.loadUrlImage(goodsCoverImg)
            holder.vName.text = goodsName
            holder.vCount.text =
                context.resources.getString(R.string.mall_goods_count, goodsCount.toString())
            holder.vPrice.text = context.resources.getString(
                R.string.mall_rmb_price,
                sellingPrice.toString()
            )
            holder.itemView.click {
                clickItemCallback(item)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vImage: ImageView = view.findViewById(R.id.image)
        val vName: TextView = view.findViewById(R.id.name)
        val vCount: TextView = view.findViewById(R.id.count)
        val vPrice: TextView = view.findViewById(R.id.total_price)
    }
}