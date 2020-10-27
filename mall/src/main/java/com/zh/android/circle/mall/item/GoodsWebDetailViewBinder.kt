package com.zh.android.circle.mall.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.model.GoodsWebDetailModel
import com.zh.android.circle.mall.ui.widget.webview.MallDetailWebView
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/10/19
 */
class GoodsWebDetailViewBinder(
    private val activity: FragmentActivity
) :
    ItemViewBinder<GoodsWebDetailModel, GoodsWebDetailViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.mall_goods_web_detail_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: GoodsWebDetailModel) {
        item.run {
            holder.vWebView.run {
                //开始配置
                setup(activity, goodsDetailContent)
                //开始加载
                loadDetail()
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vWebView: MallDetailWebView = view.findViewById(R.id.web_view)
    }
}