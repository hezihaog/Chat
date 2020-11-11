package com.zh.android.circle.todo.http

import com.lzy.okgo.OkGo
import com.lzy.okgo.request.GetRequest
import com.lzy.okgo.request.PostRequest
import com.lzy.okrx2.adapter.ObservableBody
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.genericGsonType
import com.zh.android.base.ext.toJson
import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.ModelConvert
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
class TodoRequester {
    companion object {
        /**
         * 按类型，获取Todo列表
         */
        fun getTodoListByStatus(
            tag: String,
            userId: String
        ): Observable<HttpModel<TodoListByStatusModel>> {
            val type = genericGsonType<HttpModel<TodoListByStatusModel>>()
            val request: GetRequest<HttpModel<TodoListByStatusModel>> =
                OkGo.get(ApiUrl.TODO_GET_TODO_LIST_BY_STATUS)
            return request.tag(tag)
                .params("userId", userId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 获取代办事项信息
         * @param id 代办事项Id
         */
        fun getTodoById(
            tag: String,
            id: String
        ): Observable<HttpModel<TodoModel>> {
            val type = genericGsonType<HttpModel<TodoModel>>()
            val request: GetRequest<HttpModel<TodoModel>> =
                OkGo.get(ApiUrl.TODO_GET_TODO_BY_ID)
            return request.tag(tag)
                .params("id", id)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
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
            tag: String,
            userId: String,
            title: String,
            content: String?,
            date: Date?,
            type: TodoType,
            priority: TodoPriority
        ): Observable<HttpModel<*>> {
            val modelType = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.TODO_ADD_TODO)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("userId", userId)
                    put("title", title)
                    if (content != null) {
                        put("content", content)
                    }
                    if (date != null) {
                        put("date", date.time)
                    }
                    put("type", type.code)
                    put("priority", priority.code)
                }.toJson())
                .converter(ModelConvert(modelType))
                .adapt(ObservableBody())
        }

        /**
         * 更新一个Todo
         * @param title 标题
         * @param content 内容，非必填
         * @param date 预计完成时间，不传，则默认今天
         * @param type 类型，工作1，生活2，娱乐3
         * @param priority 优先级，重要1，一般2
         */
        fun updateTodo(
            tag: String,
            todoId: String,
            userId: String,
            title: String,
            content: String?,
            date: Date?,
            type: TodoType,
            priority: TodoPriority
        ): Observable<HttpModel<*>> {
            val modelType = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.TODO_UPDATE_TODO)
            return request.tag(tag)
                .upJson(LinkedHashMap<String, Any>().apply {
                    put("id", todoId)
                    put("userId", userId)
                    put("title", title)
                    if (content != null) {
                        put("content", content)
                    }
                    if (date != null) {
                        put("date", date.time)
                    }
                    put("type", type.code)
                    put("priority", priority.code)
                }.toJson())
                .converter(ModelConvert(modelType))
                .adapt(ObservableBody())
        }

        /**
         * 删除一个Todo
         */
        fun deleteTodo(
            tag: String,
            todoId: String,
            userId: String
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.TODO_DELETE_TODO)
            return request.tag(tag)
                .params("id", todoId)
                .params("userId", userId)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 更新代办事项的状态
         * @param newStatus 新状态
         */
        fun updateTodoStatus(
            tag: String,
            todoId: String,
            userId: String,
            newStatus: TodoStatus
        ): Observable<HttpModel<*>> {
            val type = genericGsonType<HttpModel<*>>()
            val request: PostRequest<HttpModel<*>> =
                OkGo.post(ApiUrl.TODO_UPDATE_TODO_STATUS)
            return request.tag(tag)
                .params("id", todoId)
                .params("userId", userId)
                .params("newStatus", newStatus.code)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }
    }
}