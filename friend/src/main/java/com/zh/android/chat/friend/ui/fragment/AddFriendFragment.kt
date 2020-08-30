package com.zh.android.chat.friend.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.friend.R
import com.zh.android.chat.friend.http.FriendPresenter
import com.zh.android.chat.service.module.friend.FriendService
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/08/27
 * 添加好友
 */
class AddFriendFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.FRIEND_SERVICE)
    var mFriendService: FriendService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vInput: EditText by bindView(R.id.input)

    companion object {
        fun newInstance(args: Bundle? = Bundle()): AddFriendFragment {
            val fragment = AddFriendFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val mFriendPresenter by lazy {
        FriendPresenter()
    }

    override fun onInflaterViewId(): Int {
        return R.layout.friend_add_friend_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(getString(R.string.friend_add_friend))
            addRightTextButton(R.string.friend_search, R.id.friend_search).click {
                val keyword = vInput.text.toString().trim()
                if (keyword.isBlank()) {
                    toast(R.string.friend_search_tip)
                    return@click
                }
                mFriendPresenter.findUserByUsername(keyword)
                    .ioToMain()
                    .lifecycle(lifecycleOwner)
                    .subscribe({ httpModel ->
                        if (handlerErrorCode(httpModel)) {
                            httpModel.data?.let {
                                //跳转到用户资料页面
                                mFriendService?.goUserProfile(fragmentActivity, it.id)
                            }
                        }
                    }, { error ->
                        error.printStackTrace()
                        showRequestError()
                    })
            }
        }
    }
}