package com.zh.android.chat.mine.ui.fragment

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.mine.R
import com.zh.android.chat.mine.http.MinePresenter
import com.zh.android.chat.mine.item.MineImageItemViewBinder
import com.zh.android.chat.mine.item.MineTextItemViewBinder
import com.zh.android.chat.mine.model.MineImageItemModel
import com.zh.android.chat.mine.model.MineTextItemModel
import com.zh.android.chat.service.module.login.LoginService
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/08/26
 * 我的模块首页
 */
class MineFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vLogout: TextView by bindView(R.id.logout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    private val mMinePresenter by lazy {
        MinePresenter()
    }

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            //文字Item
            register(MineTextItemModel::class.java, MineTextItemViewBinder { _, item ->
                when (item.itemId) {
                    //昵称
                    R.id.mine_item_nickname -> {
                        toast("修改昵称")
                    }
                }
            })
            //图片Item
            register(MineImageItemModel::class.java, MineImageItemViewBinder { _, item ->
                when (item.itemId) {
                    R.id.mine_item_avatar -> {
                        toast("修改头像")
                    }
                    R.id.mine_item_qrcode -> {
                        toast("查看二维码")
                    }
                }
            })
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.mine_mine_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            setTitle(R.string.mine_mine)
        }
        vRefreshList.apply {
            layoutManager = LinearLayoutManager(fragmentActivity)
            adapter = mListAdapter
        }
        vLogout.click {
            mLoginService?.logout()
        }
    }

    override fun setData() {
        super.setData()
        //先渲染空数据
        render("", "", "", "")
        //再拉取远程数据
        mLoginService?.run {
            val userId = getUserId()
            mMinePresenter.getUserInfo(userId)
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({ httpModel ->
                    if (handlerErrorCode(httpModel)) {
                        httpModel.result?.let {
                            render(
                                it.picNormal,
                                it.nickname,
                                it.username,
                                it.qrCode ?: ""
                            )
                        }
                    }
                }, { error ->
                    error.printStackTrace()
                })
        }
    }

    /**
     * 渲染数据
     * @param avatar
     */
    private fun render(avatar: String, nickname: String, account: String, qrCode: String) {
        mListItems.clear()
        //头像
        mListItems.add(
            MineImageItemModel(
                R.id.mine_item_avatar,
                getString(R.string.mine_avatar),
                avatar,
                R.drawable.mine_default_user_avatar,
                true
            )
        )
        //昵称
        mListItems.add(
            MineTextItemModel(
                R.id.mine_item_nickname,
                getString(R.string.mine_nickname),
                nickname,
                true
            )
        )
        //账号
        mListItems.add(
            MineTextItemModel(
                R.id.mine_item_account,
                getString(R.string.mine_account),
                account
            )
        )
        //二维码
        mListItems.add(
            MineImageItemModel(
                R.id.mine_item_qrcode,
                getString(R.string.mine_qrcode),
                qrCode,
                R.drawable.mine_qrcode,
                true
            )
        )
        mListAdapter.notifyDataSetChanged()
    }
}