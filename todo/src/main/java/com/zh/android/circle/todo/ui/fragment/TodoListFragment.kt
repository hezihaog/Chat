package com.zh.android.circle.todo.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.BroadcastRegistry
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.todo.TodoService
import com.zh.android.circle.todo.R
import com.zh.android.circle.todo.enums.TodoStatus
import com.zh.android.circle.todo.http.TodoPresenter
import com.zh.android.circle.todo.item.TodoGroupViewBinder
import com.zh.android.circle.todo.model.TodoGroupModel
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/11/11
 * Todo列表
 */
class TodoListFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.TODO_SERVICE)
    var mTodoService: TodoService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)
    private val vAddTodo: FloatingActionButton by bindView(R.id.add_todo)

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            //代办事项分组
            register(TodoGroupModel::class.java, TodoGroupViewBinder(
                { position, model ->
                    model.isExpand = !model.isExpand
                    notifyItemChanged(position)
                }, { model ->
                    val newStatus = when (model.status) {
                        //将未完成，变为已完成
                        TodoStatus.UNFINISHED.code -> {
                            TodoStatus.FINISHED
                        }
                        //已完成变为未完成
                        TodoStatus.FINISHED.code -> {
                            TodoStatus.UNFINISHED
                        }
                        else -> {
                            null
                        }
                    } ?: return@TodoGroupViewBinder
                    updateTodoStatus(model.id, newStatus)
                }, { model ->
                    //跳转去编辑
                    mTodoService?.goTodoEdit(fragmentActivity, model.id)
                },
                { model ->
                    AlertDialog.Builder(fragmentActivity)
                        .setMessage(R.string.todo_delete_tip)
                        .setPositiveButton(R.string.base_confirm) { _, _ ->
                            //删除
                            deleteTodo(model.id)
                        }
                        .setNegativeButton(R.string.base_cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                    true
                }
            ))
        }
    }

    private val mTodoPresenter by lazy {
        TodoPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): TodoListFragment {
            val fragment = TodoListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BroadcastRegistry(lifecycleOwner).register(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                refresh()
            }
        }, AppConstant.Action.TODO_REFRESH_TODO_LIST)
    }

    override fun onInflaterViewId(): Int {
        return R.layout.todo_list_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.todo_module_name)
        }
        vRefreshLayout.apply {
            setOnRefreshListener {
                refresh()
            }
            setEnableLoadMore(false)
        }
        vRefreshList.apply {
            layoutManager = LinearLayoutManager(fragmentActivity)
            adapter = mListAdapter
            itemAnimator = null
        }
        vAddTodo.click {
            //新增Todo
            mTodoService?.goTodoAdd(fragmentActivity)
        }
    }

    override fun setData() {
        super.setData()
        refresh()
    }

    private fun refresh() {
        getTodoListByStatus()
    }

    /**
     * 按类型，获取Todo列表
     */
    private fun getTodoListByStatus() {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mTodoPresenter.getTodoListByStatus(userId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                vRefreshLayout.finishRefresh()
                if (handlerErrorCode(httpModel)) {
                    httpModel.data?.let {
                        mListItems.apply {
                            clear()
                            //未完成
                            add(
                                TodoGroupModel(
                                    getString(
                                        R.string.todo_unfinished,
                                        it.unfinished.size.toString()
                                    ),
                                    TodoStatus.UNFINISHED,
                                    it.unfinished
                                )
                            )
                            //已完成
                            add(
                                TodoGroupModel(
                                    getString(
                                        R.string.todo_finished,
                                        it.finished.size.toString()
                                    ),
                                    TodoStatus.FINISHED,
                                    it.finished
                                )
                            )
                        }
                        mListAdapter.notifyDataSetChanged()
                    }
                }
            }, {
                it.printStackTrace()
                vRefreshLayout.finishRefresh(false)
                showRequestError()
            })
    }

    /**
     * 更新代办事项的状态
     * @param newStatus 新状态
     */
    private fun updateTodoStatus(
        todoId: String,
        newStatus: TodoStatus
    ) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mTodoPresenter.updateTodoStatus(todoId, userId, newStatus)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    refresh()
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }

    /**
     * 删除一个Todo
     */
    private fun deleteTodo(
        todoId: String
    ) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mTodoPresenter.deleteTodo(todoId, userId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    toast(R.string.base_delete_success)
                    refresh()
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }
}