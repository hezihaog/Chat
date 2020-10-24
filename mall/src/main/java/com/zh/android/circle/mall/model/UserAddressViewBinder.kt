package com.zh.android.circle.mall.model

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.setGone
import com.zh.android.base.ext.setVisible
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.enums.DefaultAddressFlag
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
            holder.vName.text = "$userName $userPhone"
            holder.vAddress.text = "$provinceName $cityName $regionName $detailAddress"
            holder.itemView.click {
                onItemClickCallback(item)
            }
            holder.vDefaultSymbol.apply {
                if (defaultFlag == DefaultAddressFlag.IS_DEFAULT.code) {
                    setVisible()
                } else {
                    setGone()
                }
            }
            holder.vEditSymbol.apply {
                if (isEdit) {
                    setVisible()
                } else {
                    setGone()
                }
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vName: TextView = view.findViewById(R.id.name)
        val vAddress: TextView = view.findViewById(R.id.address)
        val vDefaultSymbol: View = view.findViewById(R.id.default_symbol)
        val vEditSymbol: View = view.findViewById(R.id.edit_symbol)
    }
}