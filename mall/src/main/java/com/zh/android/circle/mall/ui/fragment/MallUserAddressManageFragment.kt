package com.zh.android.circle.mall.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.http.MallPresenter
import com.zh.android.circle.mall.model.UserAddressModel
import com.zh.android.circle.mall.model.UserAddressViewBinder
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/21
 * 用户收货地址管理
 */
class MallUserAddressManageFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)
    private val vAddUserAddress: TextView by bindView(R.id.add_user_address)

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(UserAddressModel::class.java, UserAddressViewBinder {
                //跳转去编辑地址
            })
        }
    }

    private val mMallPresenter by lazy {
        MallPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MallUserAddressManageFragment {
            val fragment = MallUserAddressManageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.mall_user_address_manage_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.mall_my_address)
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
        }
        vAddUserAddress.click {
            //跳转去添加地址
        }
    }

    override fun setData() {
        super.setData()
        refresh()
    }

    private fun refresh() {
        getMyAddressList()
    }

    /**
     * 获取我的收货地址列表
     */
    private fun getMyAddressList() {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        mMallPresenter.getMyAddressList(userId)
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                vRefreshLayout.finishRefresh()
                if (handlerErrorCode(httpModel)) {
                    val list = httpModel.data ?: mutableListOf()
                    mListItems.clear()
                    mListItems.addAll(list)
                    mListAdapter.notifyDataSetChanged()
                }
            }, { error ->
                error.printStackTrace()
                vRefreshLayout.finishRefresh(false)
                showRequestError()
            })
    }
}