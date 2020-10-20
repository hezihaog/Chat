package com.zh.android.circle.mall.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.base.ext.longClick
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.model.ShoppingCartItemModel
import com.zh.android.circle.mall.ui.widget.MallStepper
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/10/20
 * 购物车项条目
 */
class ShoppingCartItemViewBinder(
    /**
     * 当切换选中时，回调
     */
    private val onToggleSelectCallback: (model: ShoppingCartItemModel) -> Unit,
    /**
     * 当点击减少时回调
     */
    private val onClickDecrementCallback: (model: ShoppingCartItemModel, newValue: Int) -> Unit,
    /**
     * 当点击增加时回调
     */
    private val onClickIncrementCallback: (model: ShoppingCartItemModel, newValue: Int) -> Unit,
    /**
     * 当点击条目时回调
     */
    private val onClickItemCallback: (item: ShoppingCartItemModel) -> Unit,
    /**
     * 当长按条目时回调
     */
    private val onLongClickItem: (model: ShoppingCartItemModel) -> Boolean
) :
    ItemViewBinder<ShoppingCartItemModel, ShoppingCartItemViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.mall_shopping_card_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: ShoppingCartItemModel) {
        val context = holder.itemView.context
        item.run {
            holder.vSelect.run {
                if (isSelect) {
                    setImageResource(R.drawable.base_select)
                } else {
                    setImageResource(R.drawable.base_un_select)
                }
                click {
                    onToggleSelectCallback(item)
                }
            }
            holder.vImage.loadUrlImage(goodsCoverImg)
            holder.vName.text = goodsName
            holder.vCount.text =
                context.resources.getString(R.string.mall_goods_count, goodsCount.toString())
            holder.vPrice.text = context.resources.getString(
                R.string.mall_rmb_price,
                sellingPrice.toString()
            )
            holder.vStepper.apply {
                currentValue = goodsCount
                //设置步长
                setStepValue(1)
                //设置最小值
                setMinimumValue(1)
                //设置最大值
                setMaximumValue(100)
                //设置回调
                setCallback(object : MallStepper.Callback {
                    override fun onClickDecrement(currentValue: Int, newValue: Int) {
                        onClickDecrementCallback(item, newValue)
                    }

                    override fun onClickIncrement(currentValue: Int, newValue: Int) {
                        onClickIncrementCallback(item, newValue)
                    }
                })
            }
            holder.itemView.click {
                onClickItemCallback(item)
            }
            holder.itemView.longClick {
                onLongClickItem(item)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vSelect: ImageView = view.findViewById(R.id.select)
        val vImage: ImageView = view.findViewById(R.id.image)
        val vName: TextView = view.findViewById(R.id.name)
        val vCount: TextView = view.findViewById(R.id.count)
        val vPrice: TextView = view.findViewById(R.id.total_price)
        val vStepper: MallStepper = view.findViewById(R.id.stepper)
    }
}