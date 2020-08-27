package com.zh.android.chat.friend.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.chat.friend.R
import com.zh.android.chat.friend.model.LetterModel
import me.drakeet.multitype.ItemViewBinder

/**
 * @author wally
 * @date 2020/08/27
 * 字母条目
 */
class LetterViewBinder : ItemViewBinder<LetterModel, LetterViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.friend_letter_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: LetterModel) {
        item.run {
            holder.vLetter.text = letter
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vLetter: TextView = view.findViewById(R.id.letter)
    }
}