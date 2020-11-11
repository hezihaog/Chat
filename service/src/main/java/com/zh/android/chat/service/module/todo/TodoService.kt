package com.zh.android.chat.service.module.todo

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author wally
 * @date 2020/11/11
 */
interface TodoService : IProvider {
    /**
     * 跳转到Todo列表
     */
    fun goTodoList(activity: Activity);

    /**
     * 跳转到Todo添加
     */
    fun goTodoAdd(activity: Activity)

    /**
     * 跳转到Todo编辑
     * @param todoId 代办事项Id
     */
    fun goTodoEdit(activity: Activity, todoId: String)
}