package com.zh.android.circle.todo

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.core.BaseModuleService
import com.zh.android.chat.service.ext.startNavigation
import com.zh.android.chat.service.module.todo.TodoService
import com.zh.android.circle.todo.enums.TodoEditType

/**
 * @author wally
 * @date 2020/11/11
 */
@Route(path = ARouterUrl.TODO_SERVICE, name = "代办事项模块服务")
class TodoServiceImpl : BaseModuleService(), TodoService {
    override fun init(context: Context?) {
    }

    override fun goTodoList(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.TODO_LIST)
            .startNavigation(activity)
    }

    override fun goTodoListWeb(activity: Activity) {
        goInnerWebBrowser(activity, "file:///android_asset/todolist/ToDoList.html")
    }

    override fun goTodoAdd(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.TODO_EDIT)
            .withSerializable(AppConstant.Key.TODO_EDIT_TYPE, TodoEditType.ADD)
            .startNavigation(activity)
    }

    override fun goTodoEdit(activity: Activity, todoId: String) {
        ARouter.getInstance()
            .build(ARouterUrl.TODO_EDIT)
            .withString(AppConstant.Key.TODO_ID, todoId)
            .withSerializable(AppConstant.Key.TODO_EDIT_TYPE, TodoEditType.UPDATE)
            .startNavigation(activity)
    }
}