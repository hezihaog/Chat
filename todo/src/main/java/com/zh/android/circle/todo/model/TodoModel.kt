package com.zh.android.circle.todo.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/11/11
 * 待办事项条目模型
 */
data class TodoModel(
    /**
     * Todo的Id
     */
    val id: String,
    /**
     * 用户Id
     */
    val userId: String,
    /**
     * Todo标题
     */
    val title: String,
    /**
     * Todo内容
     */
    val content: String,
    /**
     * 预计完成时间，时间戳
     */
    val date: Long,
    /**
     * 预计完成时间，文字
     */
    val dateStr: String,
    /**
     * 类型，工作1，生活2，娱乐3
     */
    val type: Int,
    /**
     * 优先级，重要1，一般2
     */
    val priority: Int,
    /**
     * 状态，未完成0，已完成1
     */
    var status: Int
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}