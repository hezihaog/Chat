package com.zh.android.circle.mall.model

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.circle.mall.R
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/10/21
 * 用户地址信息条目
 */
class UserAddressViewBinder(
    private val onItemClickCallback: (model: UserAddressModel) -> Unit
) : ItemViewBinder<UserAddressModel, UserAddressViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.mall_user_address_item_view, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, item: UserAddressModel) {
        item.run {
            holder.vName.text = userName + userPhone
            holder.vAddress.text = "$provinceName $cityName $regionName $detailAddress"
            holder.itemView.click {
                onItemClickCallback(item)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vName: TextView = view.findViewById(R.id.name)
        val vAddress: TextView = view.findViewById(R.id.address)
    }
}