package com.zh.android.circle.mall.item

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.setInVisible
import com.zh.android.base.ext.setVisible
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.model.ChooseUserAddressModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/10/24
 * 选择的收货地址条目
 */
class ChooseUserAddressViewBinder(
    private val onItemClickCallback: (model: ChooseUserAddressModel) -> Unit
) :
    ItemViewBinder<ChooseUserAddressModel, ChooseUserAddressViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.mall_choose_user_address_item_view,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, item: ChooseUserAddressModel) {
        //渲染默认信息
        if (item.info == null) {
            holder.vInfoLayout.setInVisible()
            holder.vTip.setVisible()
        } else {
            //渲染选中的信息
            item.info?.run {
                holder.vInfoLayout.setVisible()
                holder.vTip.setInVisible()
                holder.vName.text = "$userName $userPhone"
                holder.vAddress.text = "$provinceName $cityName $regionName $detailAddress"
            }
        }
        holder.itemView.click {
            onItemClickCallback(item)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vInfoLayout: View = view.findViewById(R.id.info_layout)
        val vName: TextView = view.findViewById(R.id.name)
        val vAddress: TextView = view.findViewById(R.id.address)
        val vTip: TextView = view.findViewById(R.id.tip)
    }
}