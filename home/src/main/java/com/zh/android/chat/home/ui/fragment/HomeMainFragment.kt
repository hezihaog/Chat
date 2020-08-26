package com.zh.android.chat.home.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ui.fragment.BaseFragmentStateAdapter
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.widget.LingJiRadioButton
import com.zh.android.base.widget.LingJiRadioGroup
import com.zh.android.base.widget.iconfont.IconFontTextView
import com.zh.android.chat.home.R
import com.zh.android.chat.service.module.discovery.DiscoveryService
import com.zh.android.chat.service.module.friend.FriendService
import com.zh.android.chat.service.module.login.LoginService
import com.zh.android.chat.service.module.mine.MineService
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/08/26
 * 首页
 */
class HomeMainFragment : BaseFragment(), LingJiRadioGroup.OnCheckedChangeListener,
    LingJiRadioButton.OnCheckedStatusChangeListener {

    @JvmField
    @Autowired(name = ARouterUrl.LOGIN_SERVICE)
    var mLoginService: LoginService? = null

    @JvmField
    @Autowired(name = ARouterUrl.FRIEND_SERVICE)
    var mFriendService: FriendService? = null

    @JvmField
    @Autowired(name = ARouterUrl.DISCOVERY_SERVICE)
    var mDiscoveryService: DiscoveryService? = null

    @JvmField
    @Autowired(name = ARouterUrl.MINE_SERVICE)
    var mMineService: MineService? = null

    private val vPager: ViewPager2 by bindView(R.id.pager)
    private val vTabGroup: LingJiRadioGroup by bindView(R.id.tab_group)

    private val vTabHome: LingJiRadioButton by bindView(R.id.tab_conversation)
    private val vTabWork: LingJiRadioButton by bindView(R.id.tab_friend)
    private val vTabNotice: LingJiRadioButton by bindView(R.id.tab_discovery)
    private val vTabMine: LingJiRadioButton by bindView(R.id.tab_mine)

    private val mLoadingController by lazy {
        WaitLoadingController(hostActivity, fragment)
    }

    /**
     * Tab位置
     */
    private enum class Tab(val index: Int, val tabId: Int) {
        CONVERSATION(0, R.id.tab_conversation),
        FRIEND(1, R.id.tab_friend),
        DISCOVERY(2, R.id.tab_discovery),
        MINE(3, R.id.tab_mine);
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): HomeMainFragment {
            val fragment = HomeMainFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.home_home_fragment
    }

    override fun onBindView(view: View?) {
        setupTab()
    }


    /**
     * 配置Tab
     */
    private fun setupTab() {
        val tabItems = mutableListOf<BaseFragmentStateAdapter.TabInfo>()
        //配置TabFragment
        mFriendService?.let {
            tabItems.add(
                BaseFragmentStateAdapter.TabInfo(
                    it.getFriendMainFragment()
                )
            )
        }
        mDiscoveryService?.let {
            tabItems.add(BaseFragmentStateAdapter.TabInfo(it.getDiscoveryFragment()))
        }
        mMineService?.let {
            tabItems.add(BaseFragmentStateAdapter.TabInfo(it.getMineFragment()))
        }
        vPager.run {
            //禁用滑动切换
            isUserInputEnabled = false
            adapter = BaseFragmentStateAdapter(context!!, childFragmentManager, lifecycle, tabItems)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    cleanListenerSetSelected(position)
                }
            })
            //缓存所有Tab，切换时，不重新创建
            vPager.offscreenPageLimit = tabItems.count()
        }
        //设置切换回调
        vTabHome.onCheckedStatusChangeListener = this
        vTabWork.onCheckedStatusChangeListener = this
        vTabNotice.onCheckedStatusChangeListener = this
        vTabMine.onCheckedStatusChangeListener = this
        //单选按钮点击切换监听
        vTabGroup.setOnCheckedChangeListener(this)
        //默认选中第一个Tab
        vTabGroup.setCheckButton(Tab.CONVERSATION.tabId)
        vTabGroup.setOnDoubleCheckListener(object : LingJiRadioGroup.OnDoubleCheckListener {
            override fun onDoubleCheck(
                group: LingJiRadioGroup?,
                button: LingJiRadioButton?
            ): Boolean {
                return false
            }

            override fun onDoubleCheckFinish(
                group: LingJiRadioGroup?,
                button: LingJiRadioButton?,
                isChecked: Boolean
            ) {
            }
        })
    }

    /**
     * ViewPager切换，同步切换单选按钮
     */
    private fun cleanListenerSetSelected(position: Int) {
        vTabGroup.let {
            it.setOnCheckedChangeListener(null)
            when (position) {
                Tab.CONVERSATION.index -> vTabGroup.setCheckButton(Tab.CONVERSATION.tabId)
                Tab.FRIEND.index -> vTabGroup.setCheckButton(Tab.FRIEND.tabId)
                Tab.DISCOVERY.index -> vTabGroup.setCheckButton(Tab.DISCOVERY.tabId)
                Tab.MINE.index -> vTabGroup.setCheckButton(Tab.MINE.tabId)
            }
            it.setOnCheckedChangeListener(this)
        }
    }

    override fun onCheckChange(
        group: LingJiRadioGroup?,
        checkedButton: LingJiRadioButton?,
        uncheckButton: LingJiRadioButton?
    ) {
        checkedButton?.let { btn ->
            val index = when (btn.id) {
                R.id.tab_conversation -> {
                    Tab.CONVERSATION.index
                }
                R.id.tab_friend -> {
                    Tab.FRIEND.index
                }
                R.id.tab_discovery -> {
                    Tab.DISCOVERY.index
                }
                R.id.tab_mine -> {
                    Tab.MINE.index
                }
                else -> {
                    Tab.CONVERSATION.index
                }
            }
            if (vPager.currentItem != index) {
                vPager.setCurrentItem(index, false)
            }
        }
    }

    /**
     * 单选按钮状态改变
     */
    override fun onCheckedStatusChangeListener(button: LingJiRadioButton?, isChecked: Boolean) {
        button?.let {
            //Tab的Icon
            val tabIconView: IconFontTextView? = it.findViewById(R.id.tab_icon)
            //Tab的文字
            val tabTextView: View? = it.findViewById(R.id.tab_text)
            tabIconView?.apply {
                isSelected = isChecked
            }
            tabTextView?.apply {
                isSelected = isChecked
            }
        }
    }
}