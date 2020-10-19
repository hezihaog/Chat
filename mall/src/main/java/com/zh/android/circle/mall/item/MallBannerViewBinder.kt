package com.zh.android.circle.mall.item

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.rd.PageIndicatorView
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.click
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.model.MallBannerModel
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/10/17
 * 商城轮播图条目
 */
class MallBannerViewBinder(
    private val onClickItemCallback: (model: MallBannerModel.CarouselModel) -> Unit
) : ItemViewBinder<MallBannerModel, MallBannerViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.mall_index_banner_item_view, parent, false))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, item: MallBannerModel) {
        item.run {
            //指示器
            holder.vIndicator.count = list.size
            holder.vPager.apply {
                adapter = BannerAdapter()
                //隐藏自带的指示器
                setIndicatorVisibility(View.GONE)
                //自动播放
                setAutoPlay(true)
                //滚动监听
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        //切换指示器
                        holder.vIndicator.setSelected(position)
                    }
                })
                //最终，必须调用创建方法
                create()
                //设置数据
                refreshData(list)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vPager: BannerViewPager<MallBannerModel.CarouselModel, BannerViewHolder> =
            view.findViewById(R.id.pager)
        val vIndicator: PageIndicatorView = view.findViewById(R.id.indicator)
    }

    /**
     * 轮播图适配器
     */
    inner class BannerAdapter :
        BaseBannerAdapter<MallBannerModel.CarouselModel, BannerViewHolder>() {
        override fun createViewHolder(
            parent: ViewGroup,
            itemView: View?,
            viewType: Int
        ): BannerViewHolder {
            return BannerViewHolder(itemView!!)
        }

        override fun onBind(
            holder: BannerViewHolder?,
            data: MallBannerModel.CarouselModel?,
            position: Int,
            pageSize: Int
        ) {
            holder?.run {
                bindData(data, position, pageSize)
            }
        }

        override fun getLayoutId(viewType: Int): Int {
            return R.layout.mall_index_banner_inner_item_view
        }
    }

    /**
     * 轮播图的ViewHolder
     */
    inner class BannerViewHolder(view: View) : BaseViewHolder<MallBannerModel.CarouselModel>(view) {
        private val vImage: ImageView = view.findViewById(R.id.image)

        override fun bindData(data: MallBannerModel.CarouselModel?, position: Int, pageSize: Int) {
            data?.run {
                vImage.loadUrlImage(
                    ApiUrl.getFullFileUrl(data.carouselUrl)
                )
                itemView.click {
                    onClickItemCallback(data)
                }
            }
        }
    }
}