package com.zh.android.chat.discovery.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.longClick
import com.zh.android.chat.discovery.R
import com.zh.android.chat.discovery.model.QrCodeScanHistoryModel
import me.drakeet.multitype.ItemViewBinder
import org.joda.time.DateTime

/**
 * @author wally
 * @date 2021/02/21
 * 二维码扫描历史条目
 */
class QrCodeScanHistoryBinder(
    private val itemClickBlock: (position: Int, model: QrCodeScanHistoryModel) -> Unit,
    private val itemLongClickBlock: (position: Int, model: QrCodeScanHistoryModel) -> Boolean
) :
    ItemViewBinder<QrCodeScanHistoryModel, QrCodeScanHistoryBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.discovery_qr_code_scan_history_item_view,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: QrCodeScanHistoryModel) {
        item.run {
            holder.vContent.text = item.qrCodeContent
            //创建时间字符串转换
            val createTimeStr = if (item.createTime == null) {
                ""
            } else {
                DateTime(item.createTime!!.time).toString("yyyy.MM.dd HH:mm:ss")
            }
            holder.vCreateTime.text = createTimeStr
            holder.itemView.click {
                itemClickBlock(getPosition(holder), item)
            }
            holder.itemView.longClick {
                itemLongClickBlock(getPosition(holder), item)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vContent: TextView = view.findViewById(R.id.content)
        val vCreateTime: TextView = view.findViewById(R.id.create_time)
    }
}