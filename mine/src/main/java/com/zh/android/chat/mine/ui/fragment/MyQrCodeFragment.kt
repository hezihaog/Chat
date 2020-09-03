package com.zh.android.chat.mine.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.mine.R
import com.zh.android.chat.mine.http.MinePresenter
import com.zh.android.chat.service.module.login.LoginService
import com.zh.android.chat.service.module.mine.model.User
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/09/03
 * 我的二维码
 */
class MyQrCodeFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vAvatar: ImageView by bindView(R.id.avatar)
    private val vNickName: TextView by bindView(R.id.nickname)
    private val vQrCode: ImageView by bindView(R.id.qr_code)

    private val mMinePresenter by lazy {
        MinePresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MyQrCodeFragment {
            val fragment = MyQrCodeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.mine_my_qrcode_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.mine_my_qr_code)
        }
    }

    override fun setData() {
        super.setData()
        mLoginService?.run {
            val userId = getUserId()
            mMinePresenter.getUserInfo(userId)
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({ httpModel ->
                    if (handlerErrorCode(httpModel)) {
                        httpModel.data?.let {
                            render(it)
                        }
                    }
                }, { error ->
                    error.printStackTrace()
                })
        }
    }

    /**
     * 渲染信息
     */
    private fun render(user: User) {
        user.run {
            vAvatar.loadUrlImageToRound(ApiUrl.getFullImageUrl(picNormal))
            vNickName.text = nickname
            vQrCode.loadUrlImage(ApiUrl.getFullImageUrl(qrCode))
        }
    }
}