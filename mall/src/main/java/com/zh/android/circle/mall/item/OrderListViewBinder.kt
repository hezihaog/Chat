package com.zh.android.circle.mall.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.model.OrderItemModel
import com.zh.android.circle.mall.model.OrderListModel
import me.drakeet.multitype.ItemViewBinder
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/21
 * 订单列表条目
 */
class OrderListViewBinder(
    private val itemClickCallback: (model: OrderListModel) -> Unit
) : ItemViewBinder<OrderListModel, OrderListViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.mall_order_list_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: OrderListModel) {
        val context = holder.itemView.context
        item.run {
            holder.vCreateTime.text =
                context.resources.getString(R.string.mall_order_create_time, createTime)
            holder.vOrderStatus.text = orderStatusString
            //订单项列表
            holder.vOrderItemList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = MultiTypeAdapter(Items(orderItems)).apply {
                    register(OrderItemModel::class.java, InnerViewBinder(item))
                }
            }
            holder.itemView.click {
                itemClickCallback(item)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vCreateTime: TextView = view.findViewById(R.id.create_time)
        val vOrderStatus: TextView = view.findViewById(R.id.order_status)
        val vOrderItemList: RecyclerView = view.findViewById(R.id.order_item_list)
    }

    /**
     * 订单里的订单项列表
     */
    inner class InnerViewBinder(
        private val orderInfo: OrderListModel
    ) :
        ItemViewBinder<OrderItemModel, InnerViewBinder.InnerViewHolder>() {
        override fun onCreateViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup
        ): InnerViewHolder {
            return InnerViewHolder(
                inflater.inflate(
                    R.layout.mall_order_list_inner_order_item_view,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: InnerViewHolder, item: OrderItemModel) {
            val context = holder.itemView.context
            item.run {
                holder.vImage.loadUrlImage(goodsCoverImg)
                holder.vName.text = goodsName
                holder.vPrice.text = sellingPrice.toString()
                holder.vCount.text =
                    context.resources.getString(R.string.mall_goods_count, goodsCount.toString())
                holder.itemView.click {
                    itemClickCallback(orderInfo)
                }
            }
        }

        inner class InnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val vImage: ImageView = view.findViewById(R.id.image)
            val vName: TextView = view.findViewById(R.id.name)
            val vPrice: TextView = view.findViewById(R.id.price)
            val vCount: TextView = view.findViewById(R.id.count)
        }
    }
}