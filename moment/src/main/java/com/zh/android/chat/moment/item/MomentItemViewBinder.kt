package com.zh.android.chat.moment.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lzy.ninegrid.ImageInfo
import com.lzy.ninegrid.NineGridView
import com.lzy.ninegrid.preview.NineGridViewClickAdapter
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.base.ext.setGone
import com.zh.android.base.ext.setVisible
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.model.MomentModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/09/19
 * 动态列表
 */
class MomentItemViewBinder : ItemViewBinder<MomentModel, MomentItemViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.moment_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: MomentModel) {
        val context = holder.itemView.context
        item.run {
            holder.avatar.loadUrlImage(ApiUrl.getFullImageUrl(userInfo.picNormal))
            holder.nickname.text = userInfo.nickname
            holder.createTime.text = createTime
            holder.content.text = content
            if (pictures.isNotEmpty()) {
                holder.nineGridView.setVisible()
                //图片信息
                val imageInfoList = mutableListOf<ImageInfo>().apply {
                    addAll(
                        pictures.map {
                            val info = ImageInfo()
                            info.bigImageUrl = ApiUrl.getFullImageUrl(it)
                            info.thumbnailUrl = ApiUrl.getFullImageUrl(it)
                            info
                        }
                    )
                }
                //适配器
                holder.nineGridView.setAdapter(NineGridViewClickAdapter(context, imageInfoList))
            } else {
                holder.nineGridView.setGone()
            }
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val nickname: TextView = view.findViewById(R.id.nickname)
        val createTime: TextView = view.findViewById(R.id.create_time)
        val content: TextView = view.findViewById(R.id.content)
        val nineGridView: NineGridView = view.findViewById(R.id.nine_grid_view)
    }
}