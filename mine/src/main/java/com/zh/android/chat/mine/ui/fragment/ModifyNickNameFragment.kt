package com.zh.android.chat.mine.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.linghit.base.util.argument.bindArgument
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.mine.R
import com.zh.android.chat.mine.http.MinePresenter
import com.zh.android.chat.service.AppConstant
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/08/27
 * 修改昵称
 */
class ModifyNickNameFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vNickNameInput: EditText by bindView(R.id.nickname_input)

    private val mUserId: String by bindArgument(AppConstant.Key.USER_ID, "")
    private val mOriginNickName: String by bindArgument(AppConstant.Key.NICK_NAME, "")

    private val mMinePresenter by lazy {
        MinePresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): ModifyNickNameFragment {
            val fragment = ModifyNickNameFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.mine_modify_nickname_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.mine_modify_nickname)
            addRightTextButton(R.string.mine_save, R.id.mine_topview_save).click {
                val newNickName = vNickNameInput.text.toString().trim()
                if (newNickName.isBlank()) {
                    toast(R.string.mine_nickname_input_hint)
                    return@click
                }
                mMinePresenter.updateNickName(mUserId, newNickName)
                    .ioToMain()
                    .lifecycle(lifecycleOwner)
                    .subscribe({ httpModel ->
                        if (handlerErrorCode(httpModel)) {
                            //通知更新昵称
                            AppBroadcastManager.sendBroadcast(AppConstant.Action.UPDATE_NICKNAME,
                                Intent().apply {
                                    putExtra(AppConstant.Key.NICK_NAME, newNickName)
                                }
                            )
                            toast(R.string.mine_modify_success)
                            fragmentActivity.finish()
                        }
                    }, { error ->
                        error.printStackTrace()
                        showRequestError()
                    })
            }
        }
    }

    override fun setData() {
        super.setData()
        vNickNameInput.run {
            setTextWithSelection(mOriginNickName)
        }
    }
}