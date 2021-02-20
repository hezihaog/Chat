package com.zh.android.circle.todo.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.loadMainFragment
import com.zh.android.circle.todo.R
import com.zh.android.circle.todo.ui.fragment.TodoEditFragment

/**
 * @author wally
 * @date 2020/11/11
 * 代办事项编辑
 */
@Route(path = ARouterUrl.TODO_EDIT)
class TodoEditActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        loadMainFragment(
            R.id.base_container, TodoEditFragment.newInstance(
                intent?.extras
            )
        )
    }
}