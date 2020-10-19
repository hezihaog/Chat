package com.zh.android.circle.mall.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ycbjie.webviewlib.view.X5WebView
import com.zh.android.base.ext.setGone
import com.zh.android.base.ext.setVisible
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.model.GoodsWebDetailModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/10/19
 */
class GoodsWebDetailViewBinder :
    ItemViewBinder<GoodsWebDetailModel, GoodsWebDetailViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.mall_goods_web_detail_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: GoodsWebDetailModel) {
        item.run {
            //如果是HTML，则加载HTML信息
            if (goodsDetailContent.startsWith("<div")) {
                holder.vWebView.run {
                    setVisible()
                    loadData(goodsDetailContent, "text/html", "UTF-8")
                }
                holder.vTextContent.setGone()
            } else {
                //否则直接用文字显示
                holder.vTextContent.run {
                    setVisible()
                    text = goodsDetailContent
                }
                holder.vWebView.setGone()
            }
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vTextContent: TextView = view.findViewById(R.id.text_content)
        val vWebView: X5WebView = view.findViewById(R.id.web_view)
    }
}