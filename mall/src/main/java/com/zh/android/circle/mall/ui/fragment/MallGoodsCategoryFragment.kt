package com.zh.android.circle.mall.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.widget.TopBar
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.http.MallPresenter
import com.zh.android.circle.mall.item.GoodsCategoryFirstLevelViewBinder
import com.zh.android.circle.mall.item.GoodsCategorySecondViewBinder
import com.zh.android.circle.mall.model.MallGoodsCategoryModel
import com.zh.android.circle.mall.ui.widget.MallSearchBar
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/19
 * 商品分类
 */
class MallGoodsCategoryFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vSearchBar: MallSearchBar by bindView(R.id.search_bar)
    private val vCategoryList: RecyclerView by bindView(R.id.category_list)
    private val vChildList: RecyclerView by bindView(R.id.child_list)

    /**
     * 分类
     */
    private val mCategoryItems by lazy {
        Items()
    }
    private val mCategoryAdapter by lazy {
        MultiTypeAdapter(mCategoryItems).apply {
            register(
                MallGoodsCategoryModel::class.java,
                GoodsCategoryFirstLevelViewBinder { model ->
                    //切换右边的子分类列表
                    switchChildCategory(model.childList)
                    //切换选中的大分类
                    mCategoryItems.filterIsInstance<MallGoodsCategoryModel>().map {
                        it.isSelect = it.categoryId == model.categoryId
                    }
                    notifyDataSetChanged()
                })
        }
    }

    /**
     * 子分类
     */
    private val mChildItems by lazy {
        Items()
    }
    private val mChildAdapter by lazy {
        MultiTypeAdapter(mChildItems).apply {
            register(MallGoodsCategoryModel::class.java, GoodsCategorySecondViewBinder {
                //跳转到搜索列表，并传递分类id过去
            })
        }
    }

    private val mMallPresenter by lazy {
        MallPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MallGoodsCategoryFragment {
            val fragment = MallGoodsCategoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.mall_goods_category_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.mall_goods_category)
        }
        vCategoryList.apply {
            layoutManager = LinearLayoutManager(fragmentActivity)
            adapter = mCategoryAdapter
        }
        vChildList.apply {
            layoutManager = LinearLayoutManager(fragmentActivity)
            adapter = mChildAdapter
        }
        vSearchBar.apply {
            setTip("卫生间吹风机架")
            setCallback {
                //跳转到商品搜索
            }
        }
    }

    override fun setData() {
        super.setData()
        refresh()
    }

    private fun refresh() {
        mMallPresenter.getGoodsCategory()
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    httpModel.data?.let {
                        //填充第一级分类
                        mCategoryItems.run {
                            clear()
                            //默认选中第一个分类
                            if (it.isNotEmpty()) {
                                it.first().isSelect = true
                            }
                            addAll(it)
                        }
                        //填充第二级分类
                        mChildItems.run {
                            //拿出第一个大分类中的子分类
                            val childList = if (it.isNotEmpty()) {
                                it.first().childList
                            } else {
                                listOf()
                            }
                            clear()
                            addAll(childList)
                        }
                        //刷新
                        mCategoryAdapter.notifyDataSetChanged()
                        mChildAdapter.notifyDataSetChanged()
                    }
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }

    /**
     * 切换子分类数据
     * @param childList 新的子分类数据
     */
    private fun switchChildCategory(childList: List<MallGoodsCategoryModel>) {
        mChildItems.clear()
        mChildItems.addAll(childList)
        mChildAdapter.notifyDataSetChanged()
    }
}