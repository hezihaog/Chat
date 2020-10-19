package com.zh.android.circle.mall.item

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
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
import me.drakeet.multitype.ItemViewBinder
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

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
                val items = Items(list)
                adapter = MultiTypeAdapter(items).apply {
                    register(
                        MallBannerModel.CarouselModel::class.java,
                        InnerViewBinder()
                    )
                }
                //横向滚动
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                //滚动监听
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        //切换指示器
                        holder.vIndicator.setSelected(position)
                    }
                })
                fun goNext() {
                    //到了最后一个条目，滚动回第一个
                    if (holder.vPager.currentItem == items.size - 1) {
                        setCurrentItem(0, false)
                    } else {
                        holder.vPager.currentItem += 1
                    }
                    holder.itemView.postDelayed(
                        {
                            goNext()
                        },
                        1500
                    )
                }

                val nextAction = Runnable {
                    goNext()
                }
                setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            holder.itemView.removeCallbacks(null)
                        }
                        MotionEvent.ACTION_UP -> {
                            holder.itemView.postDelayed(
                                nextAction,
                                1500
                            )
                        }
                    }
                    false
                }
                holder.itemView.postDelayed(
                    nextAction,
                    1500
                )
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vPager: ViewPager2 = view.findViewById(R.id.pager)
        val vIndicator: PageIndicatorView = view.findViewById(R.id.indicator)
    }

    inner class InnerViewBinder :
        ItemViewBinder<MallBannerModel.CarouselModel, InnerViewBinder.InnerViewHolder>() {
        override fun onCreateViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup
        ): InnerViewHolder {
            return InnerViewHolder(
                inflater.inflate(
                    R.layout.mall_index_banner_inner_item_view,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(
            holder: InnerViewHolder,
            item: MallBannerModel.CarouselModel
        ) {
            holder.vImage.loadUrlImage(
                ApiUrl.getFullFileUrl(item.carouselUrl)
            )
            holder.itemView.click {
                onClickItemCallback(item)
            }
        }

        inner class InnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val vImage: ImageView = view.findViewById(R.id.image)
        }
    }
}