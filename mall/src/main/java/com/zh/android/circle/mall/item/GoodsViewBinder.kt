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
import com.zh.android.circle.mall.model.MallGoodsModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/10/19
 * 商品条目
 */
class GoodsViewBinder(
    private val clickItemCallback: (model: MallGoodsModel) -> Unit
) : ItemViewBinder<MallGoodsModel, GoodsViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.mall_goods_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MallGoodsModel) {
        val context = holder.itemView.context
        item.run {
            holder.vImage.loadUrlImage(goodsCoverImg)
            holder.vName.text = goodsName
            holder.vIntro.text = goodsIntro
            holder.vPrice.text =
                context.resources.getString(
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
        val vIntro: TextView = view.findViewById(R.id.intro)
        val vPrice: TextView = view.findViewById(R.id.total_price)
    }
}