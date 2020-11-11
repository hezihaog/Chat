package com.zh.android.circle.todo.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/11/11
 * 代办事项列表，包含未完成、已完成
 */
data class TodoListByStatusModel(
    /**
     * 未完成事项列表
     */
    val unfinished: List<TodoModel>,
    /**
     * 已完成事项列表
     */
    val finished: List<TodoModel>
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}