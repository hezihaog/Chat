package com.zh.android.circle.mall.model

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.circle.mall.R
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/10/21
 * 订单收货地址信息条目
 */
class OrderAddressViewBinder :
    ItemViewBinder<OrderAddressModel, OrderAddressViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.mall_order_address_item_view, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, item: OrderAddressModel) {
        item.run {
            holder.vName.text = "$userName $userPhone"
            holder.vAddress.text = "$provinceName $cityName $regionName $detailAddress"
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vName: TextView = view.findViewById(R.id.name)
        val vAddress: TextView = view.findViewById(R.id.address)
    }
}