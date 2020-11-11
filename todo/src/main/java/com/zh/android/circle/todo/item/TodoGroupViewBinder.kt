package com.zh.android.circle.todo.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.ext.click
import com.zh.android.base.ext.longClick
import com.zh.android.base.ext.setGone
import com.zh.android.base.ext.setVisible
import com.zh.android.circle.todo.R
import com.zh.android.circle.todo.enums.TodoStatus
import com.zh.android.circle.todo.model.TodoGroupModel
import com.zh.android.circle.todo.model.TodoModel
import me.drakeet.multitype.ItemViewBinder
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/11/11
 * 代办事项分组条目
 */
class TodoGroupViewBinder(
    /**
     * 切换展开、收起时回调
     */
    private val onToggleExpandCallback: (position: Int, model: TodoGroupModel) -> Unit,
    /**
     * 切换状态回调
     */
    private val onToggleStatusCallback: (model: TodoModel) -> Unit,
    /**
     * 点击代办事项时回调
     */
    private val onClickItemCallback: (model: TodoModel) -> Unit,
    /**
     * 长按代办事项时回调
     */
    private val onLongClickItemCallback: (model: TodoModel) -> Boolean
) : ItemViewBinder<TodoGroupModel, TodoGroupViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.todo_group_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: TodoGroupModel) {
        val context = holder.itemView.context
        item.run {
            holder.vName.text = name
            holder.vList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = MultiTypeAdapter(Items(list)).apply {
                    register(TodoModel::class.java, TodoViewBinder(list))
                }
            }
            //展开
            if (isExpand) {
                holder.vArrow.rotation = 90f
                holder.vDivider.setVisible()
                holder.vList.setVisible()
                holder.vBottomSpace.setGone()
            } else {
                //收起
                holder.vArrow.rotation = 0f
                holder.vDivider.setGone()
                holder.vList.setGone()
                holder.vBottomSpace.setVisible()
            }
            holder.itemView.click {
                onToggleExpandCallback(getPosition(holder), item)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vArrow: ImageView = view.findViewById(R.id.arrow)
        val vName: TextView = view.findViewById(R.id.name)
        val vDivider: View = view.findViewById(R.id.divider)
        val vList: RecyclerView = view.findViewById(R.id.list)
        val vBottomSpace: View = view.findViewById(R.id.bottom_space)
    }

    inner class TodoViewBinder(
        private val todoList: List<TodoModel>
    ) : ItemViewBinder<TodoModel, TodoViewBinder.ViewHolder>() {
        override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
            return ViewHolder(inflater.inflate(R.layout.todo_item_view, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, item: TodoModel) {
            item.run {
                holder.vTitle.text = title
                holder.vContent.apply {
                    if (content.isNotBlank()) {
                        setVisible()
                        text = content
                    } else {
                        setGone()
                    }
                }
                //状态
                holder.vCheckSymbol.apply {
                    if (status == TodoStatus.UNFINISHED.code) {
                        //未完成
                        setImageResource(R.drawable.base_un_select)
                    } else if (status == TodoStatus.FINISHED.code) {
                        //已完成
                        setImageResource(R.drawable.base_select)
                    }
                    //切换状态
                    click {
                        onToggleStatusCallback(item)
                    }
                }
                holder.vDivider.run {
                    val index = todoList.indexOf(item)
                    if (index == todoList.lastIndex) {
                        //最后一个条目，隐藏分割线
                        setGone()
                    } else {
                        setVisible()
                    }
                }
                holder.itemView.click {
                    onClickItemCallback(item)
                }
                holder.itemView.longClick {
                    onLongClickItemCallback(item)
                }
            }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val vCheckSymbol: ImageView = view.findViewById(R.id.check_symbol)
            val vTitle: TextView = view.findViewById(R.id.title)
            val vContent: TextView = view.findViewById(R.id.content)
            val vDivider: View = view.findViewById(R.id.divider)
        }
    }
}