package com.zh.android.circle.todo.enums

/**
 * @author wally
 * @date 2020/11/11
 * Todo状态
 */
enum class TodoStatus(val code: Int) {
    /**
     * 未完成
     */
    UNFINISHED(0),
    /**
     * 已完成
     */
    FINISHED(1);
}