package com.zh.android.chat.service.module.base.web.ui.fragment

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
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.R
import com.zh.android.chat.service.module.base.web.http.WebPresenter
import com.zh.android.chat.service.module.base.web.item.WebCollectViewBinder
import com.zh.android.chat.service.module.base.web.model.WebCollectModel
import com.zh.android.chat.service.module.login.LoginService
import io.reactivex.Observable
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2021/02/18
 * Web收藏列表
 */
class WebCollectListFragment : BaseFragment() {
    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    private val mWaitController by lazy {
        WaitLoadingController(fragmentActivity, fragment)
    }

    private val mWebPresenter by lazy {
        WebPresenter()
    }

    private val mListItems by lazy {
        Items()
    }

    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            register(WebCollectModel::class.java, WebCollectViewBinder({ _, model ->
                //跳转网页Url
                mLoginService?.goInnerWebBrowser(fragmentActivity, model.url)
            }, { position, model ->
                //长按显示删除
                AlertDialog.Builder(fragmentActivity)
                    .setItems(
                        arrayOf(
                            getString(R.string.base_delete)
                        )
                    ) { _, which ->
                        if (which == 0) {
                            //删除
                            mWebPresenter.deleteCollectById(model.id)
                                .doOnSubscribeUi {
                                    mWaitController.showWait()
                                }
                                .ioToMain()
                                .lifecycle(lifecycleOwner)
                                .subscribe({
                                    mWaitController.hideWait()
                                    if (it) {
                                        mListItems.remove(model)
                                        fixNotifyItemRemoved(position)
                                        toast(R.string.base_delete_success)
                                    } else {
                                        toast(R.string.base_delete_fail)
                                    }
                                }, {
                                    it.printStackTrace()
                                    toast(R.string.base_delete_fail)
                                    mWaitController.hideWait()
                                })
                        }
                    }
                    .create()
                    .show()
                true
            }))
        }
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): WebCollectListFragment {
            val fragment = WebCollectListFragment()
            fragment.arguments = args
            return fragment
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
            setTitle(R.string.service_web_collect)
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
    }

    override fun setData() {
        super.setData()
        refresh()
    }

    private fun refresh() {
        mLoginService?.getUserId()?.let { userId ->
            mWebPresenter.getCollectList(userId)
                .flatMap {
                    //将数据库模型转换为UI使用的模型数据
                    Observable.fromIterable(it).map { entity ->
                        entity.run {
                            WebCollectModel(
                                id,
                                userId,
                                title,
                                url
                            )
                        }
                    }.toList().toObservable()
                }
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({
                    mListItems.clear()
                    mListItems.addAll(it)
                    mListAdapter.notifyDataSetChanged()
                    vRefreshLayout.finishRefresh()
                }, {
                    it.printStackTrace()
                    vRefreshLayout.finishRefresh(false)
                })
        }
    }
}