package com.zh.android.circle.todo.http

import com.zh.android.base.http.HttpModel
import com.zh.android.circle.todo.enums.TodoPriority
import com.zh.android.circle.todo.enums.TodoStatus
import com.zh.android.circle.todo.enums.TodoType
import com.zh.android.circle.todo.model.TodoListByStatusModel
import com.zh.android.circle.todo.model.TodoModel
import io.reactivex.Observable
import java.util.*

/**
 * @author wally
 * @date 2020/11/11
 */
class TodoPresenter {
    companion object {
        private val TAG = TodoPresenter::class.java.simpleName
    }

    /**
     * 按类型，获取Todo列表
     */
    fun getTodoListByStatus(
        userId: String
    ): Observable<HttpModel<TodoListByStatusModel>> {
        return TodoRequester.getTodoListByStatus(TAG, userId)
    }

    /**
     * 获取代办事项信息
     * @param id 代办事项Id
     */
    fun getTodoById(
        id: String
    ): Observable<HttpModel<TodoModel>> {
        return TodoRequester.getTodoById(TAG, id)
    }

    /**
     * 增加一个Todo
     * @param title 标题
     * @param content 内容，非必填
     * @param date 预计完成时间，不传，则默认今天
     * @param type 类型，工作1，生活2，娱乐3
     * @param priority 优先级，重要1，一般2
     */
    fun addTodo(
        userId: String,
        title: String,
        content: String?,
        date: Date?,
        type: TodoType,
        priority: TodoPriority
    ): Observable<HttpModel<*>> {
        return TodoRequester.addTodo(TAG, userId, title, content, date, type, priority)
    }

    /**
     * 更新一个Todo
     * @param todoId 代办事项Id
     * @param title 标题
     * @param content 内容，非必填
     * @param date 预计完成时间，不传，则默认今天
     * @param type 类型，工作1，生活2，娱乐3
     * @param priority 优先级，重要1，一般2
     */
    fun updateTodo(
        todoId: String,
        userId: String,
        title: String,
        content: String?,
        date: Date?,
        type: TodoType,
        priority: TodoPriority
    ): Observable<HttpModel<*>> {
        return TodoRequester.updateTodo(TAG, todoId, userId, title, content, date, type, priority)
    }

    /**
     * 删除一个Todo
     */
    fun deleteTodo(
        todoId: String,
        userId: String
    ): Observable<HttpModel<*>> {
        return TodoRequester.deleteTodo(TAG, todoId, userId)
    }

    /**
     * 更新代办事项的状态
     * @param newStatus 新状态
     */
    fun updateTodoStatus(
        todoId: String,
        userId: String,
        newStatus: TodoStatus
    ): Observable<HttpModel<*>> {
        return TodoRequester.updateTodoStatus(TAG, todoId, userId, newStatus)
    }
}