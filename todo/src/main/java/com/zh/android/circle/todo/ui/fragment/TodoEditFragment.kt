package com.zh.android.circle.todo.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.linghit.base.util.argument.bindArgument
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.circle.todo.R
import com.zh.android.circle.todo.enums.TodoEditType
import com.zh.android.circle.todo.enums.TodoPriority
import com.zh.android.circle.todo.enums.TodoType
import com.zh.android.circle.todo.http.TodoPresenter
import com.zh.android.circle.todo.model.TodoModel
import io.github.prototypez.savestate.core.annotation.AutoRestore
import kotterknife.bindView
import org.joda.time.DateTime
import java.util.*

/**
 * @author wally
 * @date 2020/11/11
 * 代办事项编辑
 */
class TodoEditFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vTitleInput: EditText by bindView(R.id.title_input)
    private val vContentInput: EditText by bindView(R.id.content_input)
    private val vCompleteDate: TextView by bindView(R.id.complete_date)
    private val vCompleteDateSymbol: ImageView by bindView(R.id.complete_date_symbol)
    private val vTodoTypeGroup: RadioGroup by bindView(R.id.todo_type_group)
    private val vTodoPriorityGroup: RadioGroup by bindView(R.id.todo_priority_group)

    /**
     * 代办事项的Id，更新时有值
     */
    private val mTodoId: String by bindArgument(AppConstant.Key.TODO_ID, "")

    /**
     * 编辑类型，新增或更新
     */
    private val mEditType: TodoEditType by bindArgument(
        AppConstant.Key.TODO_EDIT_TYPE,
        TodoEditType.ADD
    )

    /**
     * 预计完成时间
     */
    @AutoRestore
    var mCompleteDate: Long = 0

    private val mTodoPresenter by lazy {
        TodoPresenter()
    }
    private val mWaitController by lazy {
        WaitLoadingController(fragmentActivity, lifecycleOwner)
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): TodoEditFragment {
            val fragment = TodoEditFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.todo_edit_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(
                if (mEditType == TodoEditType.ADD) {
                    R.string.todo_add
                } else {
                    R.string.todo_update
                }
            )
            addRightTextButton(R.string.base_submit, R.id.topbar_item_submit).click {
                //提交
                submit()
            }
        }
        //预计完成时间
        vCompleteDateSymbol.click {
            activity?.let { activity ->
                hideSoftInput()
                //如果之前选择过，将从上次选择的时间开始选
                val calendar = Calendar.getInstance().apply {
                    if (mCompleteDate > 0) {
                        time = Date(mCompleteDate)
                    }
                }
                TimePickerBuilder(activity, OnTimeSelectListener { selectDate, _ ->
                    //保存选择的时间
                    mCompleteDate = selectDate.time
                    renderCompleteDate()
                })
                    //处理有些机子的NavigationBar虚拟键在DecorView里面，导致和弹窗重合的问题
                    .setDecorView(activity.findViewById(android.R.id.content))
                    //设置一开始选择处于的时间
                    .setDate(calendar)
                    //配置可选 年、月、日
                    .setType(booleanArrayOf(true, true, true, false, false, false))
                    .setSubmitColor(ActivityCompat.getColor(activity, R.color.base_blue))
                    .setCancelColor(ActivityCompat.getColor(activity, R.color.base_blue))
                    //禁止循环
                    .isCyclic(false)
                    .build()
                    .show()
            }
        }
        renderCompleteDate()
    }

    override fun setData() {
        super.setData()
        //更新时，获取内容回显
        if (mEditType == TodoEditType.UPDATE) {
            if (mTodoId.isBlank()) {
                fragmentActivity.finish()
                return
            }
            getTodoById()
        }
    }

    /**
     * 渲染页面
     */
    private fun renderPage(model: TodoModel) {
        vTitleInput.setTextWithSelection(model.title)
        vContentInput.setTextWithSelection(model.content)
        //类型
        val checkTypeId = when (model.type) {
            TodoType.WORK.code -> {
                R.id.type_work
            }
            TodoType.LIFE.code -> {
                R.id.type_life
            }
            TodoType.ENTERTAINMENT.code -> {
                R.id.type_entertainment
            }
            else -> R.id.type_work
        }
        vTodoTypeGroup.check(checkTypeId)
        //优先级
        val checkPriorityId = when (model.priority) {
            TodoPriority.IMPORTANT.code -> {
                R.id.priority_important
            }
            TodoPriority.ORDINARY.code -> {
                R.id.priority_ordinary
            }
            else -> R.id.priority_ordinary
        }
        vTodoPriorityGroup.check(checkPriorityId)
        //预计完成时间
        mCompleteDate = model.date
        vCompleteDate.text = model.dateStr
    }

    /**
     * 渲染完成时间
     */
    private fun renderCompleteDate() {
        if (mCompleteDate > 0) {
            vCompleteDate.text = DateTime(mCompleteDate).toString("yyyy.MM.dd")
        }
    }

    /**
     * 获取代办事项信息
     */
    private fun getTodoById() {
        mTodoPresenter.getTodoById(mTodoId)
            .doOnSubscribeUi {
                mWaitController.showWait()
            }
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                mWaitController.hideWait()
                if (handlerErrorCode(httpModel)) {
                    //渲染页面
                    httpModel.data?.let {
                        renderPage(it)
                    }
                }
            }, {
                it.printStackTrace()
                mWaitController.hideWait()
                showRequestError()
            })
    }

    /**
     * 提交
     */
    private fun submit() {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        val title = vTitleInput.text.toString().trim()
        if (title.isBlank()) {
            toast(R.string.todo_title_hint)
            return
        }
        val content = vContentInput.text.toString().trim()
        //预计完成时间
        val date: Date? = if (mCompleteDate == 0L) {
            null
        } else {
            Date(mCompleteDate)
        }
        //类型
        val type = when (vTodoTypeGroup.checkedRadioButtonId) {
            //工作
            R.id.type_work -> {
                TodoType.WORK
            }
            //生活
            R.id.type_life -> {
                TodoType.LIFE
            }
            //娱乐
            R.id.type_entertainment -> {
                TodoType.ENTERTAINMENT
            }
            else -> TodoType.WORK
        }
        //优先级
        val priority = when (vTodoPriorityGroup.checkedRadioButtonId) {
            R.id.priority_important -> {
                TodoPriority.IMPORTANT
            }
            R.id.priority_ordinary -> {
                TodoPriority.ORDINARY
            }
            else -> TodoPriority.ORDINARY
        }
        //新增
        if (mEditType == TodoEditType.ADD) {
            mTodoPresenter.addTodo(
                userId,
                title,
                content,
                date,
                type,
                priority
            )
        } else {
            //更新
            mTodoPresenter.updateTodo(
                mTodoId,
                userId,
                title,
                content,
                date,
                type,
                priority
            )
        }.doOnSubscribeUi {
            mWaitController.showWait()
        }.ioToMain().lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                mWaitController.hideWait()
                if (handlerErrorCode(httpModel)) {
                    toast(R.string.todo_add_success)
                    //添加成功，通知列表刷新
                    AppBroadcastManager.sendBroadcast(AppConstant.Action.TODO_REFRESH_TODO_LIST)
                    fragmentActivity.finish()
                }
            }, {
                it.printStackTrace()
                mWaitController.hideWait()
                showRequestError()
            })
    }
}