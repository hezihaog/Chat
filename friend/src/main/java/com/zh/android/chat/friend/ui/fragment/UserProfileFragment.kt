package com.zh.android.chat.friend.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.linghit.base.util.argument.bindArgument
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.friend.R
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.module.mine.MineService
import com.zh.android.chat.service.module.mine.model.User
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/08/27
 * 用户资料
 */
class UserProfileFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.MINE_SERVICE)
    var mMineService: MineService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vContentLayout: View by bindView(R.id.content_layout)
    private val vAvatar: ImageView by bindView(R.id.avatar)
    private val vNickname: TextView by bindView(R.id.nickname)
    private val vUsername: TextView by bindView(R.id.username)
    private val vAdd: TextView by bindView(R.id.add)
    private val vBack: TextView by bindView(R.id.back)

    private val mUserId: String by bindArgument(AppConstant.Key.USER_ID, "")

    /**
     * 用户信息
     */
    private var mUserInfo: User? = null

    private val mWaitController: WaitLoadingController by lazy {
        WaitLoadingController(fragmentActivity, lifecycleOwner)
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): UserProfileFragment {
            val fragment = UserProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.friend_user_profile_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
        }
        vAdd.click {

        }
        vBack.click {
            fragmentActivity.finish()
        }
    }

    override fun setData() {
        super.setData()
        mMineService?.run {
            getUserInfo(mUserId)
                .doOnSubscribeUi {
                    mWaitController.showWait()
                }
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({ httpModel ->
                    mWaitController.hideWait()
                    if (handlerErrorCode(httpModel)) {
                        httpModel.result?.let {
                            mUserInfo = it
                            render()
                        }
                    }
                }, { error ->
                    error.printStackTrace()
                    showRequestError()
                    mWaitController.hideWait()
                })
        }
    }

    /**
     * 渲染数据
     */
    private fun render() {
        mUserInfo?.run {
            vContentLayout.visibility = View.VISIBLE
            vAvatar.loadUrlImageToRound(picNormal)
            vNickname.text = nickname
            vUsername.text = getString(R.string.friend_chat_no, username)
        }
    }
}