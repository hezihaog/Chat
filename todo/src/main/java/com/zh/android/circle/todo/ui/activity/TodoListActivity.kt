package com.zh.android.circle.todo.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.circle.todo.R
import com.zh.android.circle.todo.ui.fragment.TodoListFragment

/**
 * @author wally
 * @date 2020/11/11
 * Todo列表
 */
@Route(path = ARouterUrl.TODO_LIST)
class TodoListActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(TodoListFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, TodoListFragment.newInstance(
                    intent?.extras
                )
            )
        }
    }
}