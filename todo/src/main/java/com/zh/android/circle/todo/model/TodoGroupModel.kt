package com.zh.android.circle.todo.model

import com.zh.android.circle.todo.enums.TodoStatus
import java.io.Serializable

/**
 * @author wally
 * @date 2020/11/11
 * 待办事项模型
 */
data class TodoGroupModel(
    /**
     * 分类名称
     */
    val name: String,
    /**
     * 状态
     */
    val status: TodoStatus,
    /**
     * 代办事项列表
     */
    val list: List<TodoModel>,
    /**
     * 是否展开
     */
    var isExpand: Boolean = true
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}