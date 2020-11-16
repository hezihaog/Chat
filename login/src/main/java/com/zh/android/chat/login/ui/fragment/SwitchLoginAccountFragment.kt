package com.zh.android.chat.login.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.login.R
import com.zh.android.chat.login.http.LoginPresenter
import com.zh.android.chat.login.item.LoginUserViewBinder
import com.zh.android.chat.service.db.login.entity.LoginUserEntity
import com.zh.android.chat.service.module.home.HomeService
import com.zh.android.chat.service.module.login.LoginService
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/11/14
 * 切换登录账号
 */
class SwitchLoginAccountFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    @JvmField
    @Autowired(name = ARouterUrl.HOME_SERVICE)
    var mHomeService: HomeService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(LoginUserEntity::class.java, LoginUserViewBinder(
                {
                    //点击的是当前登录的用户，不做切换处理
                    if (it.loginFlag) {
                        return@LoginUserViewBinder
                    }
                    //切换账号
                    mLoginPresenter.switchLoginUser(it.userId)
                        .lifecycle(lifecycleOwner)
                        .subscribe {
                            //切换成功，跳转回首页
                            toast(R.string.login_switch_login_user_success)
                            mHomeService?.goHome(fragmentActivity)
                        }
                }, { position, model ->
                    //不能删除正在登录的账号
                    if (model.loginFlag) {
                        return@LoginUserViewBinder false
                    }
                    //长按显示删除
                    AlertDialog.Builder(fragmentActivity)
                        .setMessage(R.string.login_is_delete_this_account_confirm)
                        .setPositiveButton(R.string.base_confirm) { _, _ ->
                            //删除
                            mLoginPresenter.deleteLoginUser(model.userId)
                                .ioToMain()
                                .lifecycle(lifecycleOwner)
                                .subscribe {
                                    //删除条目
                                    mListItems.removeAt(position)
                                    fixNotifyItemRemoved(position)
                                    toast(R.string.base_delete_success)
                                }
                        }
                        .setNegativeButton(R.string.base_cancel) { dialog, _ ->
                            dialog.dismiss()
                        }.create().show()
                    true
                }
            ))
        }
    }

    private val mLoginPresenter by lazy {
        LoginPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): SwitchLoginAccountFragment {
            return SwitchLoginAccountFragment().apply {
                arguments = args
            }
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.base_refresh_layout_with_top_bar
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.login_switch_login_account)
            //使用其他账号登录
            addRightTextButton(
                R.string.login_use_other_account,
                R.id.login_use_other_account
            ).click {
                mLoginService?.goLogin(fragmentActivity, false, isShowBackBtn = true)
            }
        }
        vRefreshLayout.apply {
            setEnableRefresh(false)
            setEnableLoadMore(false)
        }
        vRefreshList.apply {
            layoutManager = LinearLayoutManager(fragmentActivity)
            adapter = mListAdapter
        }
    }

    override fun setData() {
        super.setData()
        refresh()
    }

    private fun refresh() {
        getLoginAccountList()
    }

    /**
     * 获取登录账号列表
     */
    private fun getLoginAccountList() {
        mLoginPresenter.getAllLoginUser()
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({
                mListItems.clear()
                mListItems.addAll(it)
                mListAdapter.notifyDataSetChanged()
            }, {
                it.printStackTrace()
            })
    }
}