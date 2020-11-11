package com.zh.android.circle.todo.enums

/**
 * @author wally
 * @date 2020/11/11
 * Todo类型
 */
enum class TodoType(val code: Int) {
    /**
     * 工作
     */
    WORK(1),
    /**
     * 生活
     */
    LIFE(2),
    /**
     * 娱乐
     */
    ENTERTAINMENT(3);
}