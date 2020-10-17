package com.zh.android.circle.mall.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.RegexUtils
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.web.BrowserActivity
import com.zh.android.base.widget.TopBar
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.enums.GoodsGroupType
import com.zh.android.circle.mall.http.MallPresenter
import com.zh.android.circle.mall.item.MallBannerViewBinder
import com.zh.android.circle.mall.item.MallIndexGoodsCategoryViewBinder
import com.zh.android.circle.mall.item.MallIndexGoodsGroupViewBinder
import com.zh.android.circle.mall.model.MallBannerModel
import com.zh.android.circle.mall.model.MallIndexGoodsCategoryModel
import com.zh.android.circle.mall.model.MallIndexGoodsGroupModel
import com.zh.android.circle.mall.model.MallIndexInfoModel
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/10/16
 * 商城首页
 */
class MallMainFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshLayout: SmartRefreshLayout by bindView(R.id.base_refresh_layout)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            //轮播图
            register(MallBannerModel::class.java, MallBannerViewBinder {
                val url = it.redirectUrl
                if (url.isNotBlank() && RegexUtils.isURL(url)) {
                    BrowserActivity.start(fragmentActivity, it.redirectUrl)
                }
            })
            //商品分类
            register(MallIndexGoodsCategoryModel::class.java, MallIndexGoodsCategoryViewBinder())
            //商品分组
            register(MallIndexGoodsGroupModel::class.java, MallIndexGoodsGroupViewBinder())
        }
    }

    private val mMallPresenter by lazy {
        MallPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MallMainFragment {
            val fragment = MallMainFragment()
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
            setTitle(R.string.mall_module_name)
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
        vRefreshLayout.autoRefresh()
    }

    private fun refresh() {
        getIndexList()
    }

    private fun getIndexList() {
        mMallPresenter.indexInfos()
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                vRefreshLayout.finishRefresh()
                if (handlerErrorCode(httpModel)) {
                    httpModel.data?.let {
                        mListItems.clear()
                        mListItems.addAll(generateItems(it))
                        mListAdapter.notifyDataSetChanged()
                    }
                }
            }, { error ->
                error.printStackTrace()
                vRefreshLayout.finishRefresh(false)
                showRequestError()
            })
    }

    /**
     * 生成列表条目
     */
    private fun generateItems(model: MallIndexInfoModel): Items {
        return Items().apply {
            //轮播图条目
            if (model.carousels.isNotEmpty()) {
                add(
                    MallBannerModel(
                        model.carousels
                    )
                )
            }
            //分类列表
            add(
                MallIndexGoodsCategoryModel(
                    generateGoodsCategory()
                )
            )
            //最热商品
            if (model.hotGoods.isNotEmpty()) {
                add(
                    MallIndexGoodsGroupModel(
                        GoodsGroupType.HOT,
                        getString(R.string.mall_goods_group_hot),
                        model.hotGoods
                    )
                )
            }
            //最新商品
            if (model.newGoods.isNotEmpty()) {
                add(
                    MallIndexGoodsGroupModel(
                        GoodsGroupType.NEW,
                        getString(R.string.mall_goods_group_new),
                        model.newGoods
                    )
                )
            }
            //推荐商品
            if (model.recommendGoods.isNotEmpty()) {
                add(
                    MallIndexGoodsGroupModel(
                        GoodsGroupType.RECOMMEND,
                        getString(R.string.mall_goods_group_recommend),
                        model.recommendGoods
                    )
                )
            }
        }
    }

    /**
     * 组装商品分类列表
     */
    private fun generateGoodsCategory(): List<MallIndexGoodsCategoryModel.CategoryModel> {
        return listOf(
            MallIndexGoodsCategoryModel.CategoryModel(
                "新蜂超市",
                "http://s.weituibao.com/1583585285461/cs.png"
            ),
            MallIndexGoodsCategoryModel.CategoryModel(
                "新蜂服饰",
                "http://s.weituibao.com/1583585285468/fs.png"
            ),
            MallIndexGoodsCategoryModel.CategoryModel(
                "全球购",
                "http://s.weituibao.com/1583585285470/qq.png"
            ),
            MallIndexGoodsCategoryModel.CategoryModel(
                "新蜂生鲜",
                "http://s.weituibao.com/1583585285472/sx.png"
            ),
            MallIndexGoodsCategoryModel.CategoryModel(
                "新蜂到家",
                "http://s.weituibao.com/1583585285467/dj.png"
            ),
            MallIndexGoodsCategoryModel.CategoryModel(
                "充值缴费",
                "http://s.weituibao.com/1583585285465/cz.png"
            ),
            MallIndexGoodsCategoryModel.CategoryModel(
                "9.9元拼",
                "http://s.weituibao.com/1583585285469/pt.png"
            ),
            MallIndexGoodsCategoryModel.CategoryModel(
                "领劵",
                "http://s.weituibao.com/1583585285468/juan.png"
            ),
            MallIndexGoodsCategoryModel.CategoryModel(
                "省钱",
                "http://s.weituibao.com/1583585285471/sq.png"
            ),
            MallIndexGoodsCategoryModel.CategoryModel(
                "全部",
                "http://s.weituibao.com/1583585285470/qb.png"
            )
        )
    }
}