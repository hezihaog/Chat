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
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder
import com.zhpan.bannerview.utils.BannerUtils
import me.drakeet.multitype.ItemViewBinder


/**
 * @author wally
 * @date 2020/10/19
 * 商品详情信息条目
 */
class GoodsDetailViewBinder(
    private val onClickItemCallback: (goodsCarouselList: List<String>, position: Int, item: String) -> Unit
) : ItemViewBinder<MallGoodsModel, GoodsDetailViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.mall_goods_detail_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MallGoodsModel) {
        val context = holder.itemView.context
        item.run {
            holder.vPager.apply {
                adapter = BannerAdapter(goodsCarouselList)
                //自动播放
                setAutoPlay(true)
                //最终，必须调用创建方法
                create()
                //设置数据
                refreshData(goodsCarouselList)
            }
            holder.vName.text = goodsName
            holder.vPrice.text = context.resources.getString(
                R.string.mall_rmb_price,
                sellingPrice.toString()
            )
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vPager: BannerViewPager<String, BannerViewHolder> = view.findViewById(R.id.pager)
        val vName: TextView = view.findViewById(R.id.name)
        val vPrice: TextView = view.findViewById(R.id.total_price)
    }

    /**
     * 轮播图适配器
     */
    inner class BannerAdapter(
        private val goodsCarouselList: List<String>
    ) :
        BaseBannerAdapter<String, GoodsDetailViewBinder.BannerViewHolder>() {
        override fun createViewHolder(
            parent: ViewGroup,
            itemView: View?,
            viewType: Int
        ): GoodsDetailViewBinder.BannerViewHolder {
            return BannerViewHolder(goodsCarouselList, itemView!!)
        }

        override fun onBind(
            holder: GoodsDetailViewBinder.BannerViewHolder?,
            data: String?,
            position: Int,
            pageSize: Int
        ) {
            holder?.run {
                bindData(data, position, pageSize)
            }
        }

        override fun getLayoutId(viewType: Int): Int {
            return R.layout.mall_goods_detail_cover_item_view
        }
    }

    /**
     * 轮播图的ViewHolder
     */
    inner class BannerViewHolder(private val goodsCarouselList: List<String>, view: View) :
        BaseViewHolder<String>(view) {
        private val vImage: ImageView = view.findViewById(R.id.image)

        override fun bindData(data: String?, position: Int, pageSize: Int) {
            data?.run {
                vImage.loadUrlImage(
                    data
                )
                itemView.click {
                    val adapterPosition = adapterPosition
                    val realPosition =
                        BannerUtils.getRealPosition(true, adapterPosition, goodsCarouselList.size)
                    onClickItemCallback(goodsCarouselList, realPosition, data)
                }
            }
        }
    }
}