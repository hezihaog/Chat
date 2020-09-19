package com.zh.android.chat.moment.ui.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.core.BaseFragment
import com.zh.android.chat.moment.R
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/09/19
 * 动态评论列表
 */
class MomentCommentFragment : BaseFragment() {
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    private val mListItem by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItem).apply {

        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.base_refresh_layout
    }

    override fun onBindView(view: View?) {
        vRefreshLayout.apply {
            setEnableRefresh(false)
            setOnLoadMoreListener {
                loadMore()
            }
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
    }

    private fun loadMore() {

    }
}