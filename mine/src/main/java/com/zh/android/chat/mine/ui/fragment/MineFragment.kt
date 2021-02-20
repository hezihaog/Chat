package com.zh.android.chat.mine.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.apkfuns.logutils.LogUtils
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.BroadcastRegistry
import com.zh.android.base.util.location.RxLocation
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.mine.MineUIHelper
import com.zh.android.chat.mine.R
import com.zh.android.chat.mine.http.MinePresenter
import com.zh.android.chat.mine.item.MineImageItemViewBinder
import com.zh.android.chat.mine.item.MineTextItemViewBinder
import com.zh.android.chat.mine.model.MineImageItemModel
import com.zh.android.chat.mine.model.MineTextItemModel
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.module.login.LoginService
import com.zh.android.chat.service.module.mall.MallService
import com.zh.android.chat.service.module.mine.MineService
import com.zh.android.chat.service.module.mine.model.User
import com.zh.android.chat.service.module.moment.MomentService
import com.zh.android.chat.service.module.notice.NoticeService
import com.zh.android.chat.service.module.setting.SettingService
import io.reactivex.Observable
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

    @JvmField
    @Autowired(name = ARouterUrl.MINE_SERVICE)
    var mMineService: MineService? = null

    @JvmField
    @Autowired(name = ARouterUrl.NOTICE_SERVICE)
    var mNoticeService: NoticeService? = null

    @JvmField
    @Autowired(name = ARouterUrl.SETTING_SERVICE)
    var mSettingService: SettingService? = null

    @JvmField
    @Autowired(name = ARouterUrl.MOMENT_SERVICE)
    var mMomentService: MomentService? = null

    @JvmField
    @Autowired(name = ARouterUrl.MALL_SERVICE)
    var mMallService: MallService? = null

    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vRefreshList: RecyclerView by bindView(R.id.base_refresh_list)

    /**
     * 用户信息
     */
    private var mUserInfo: User? = null

    private val mMinePresenter by lazy {
        MinePresenter()
    }

    private val mRxLocation by lazy {
        RxLocation()
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
                        mUserInfo?.let {
                            MineUIHelper.goModifyNickname(fragmentActivity, it.id, it.nickname)
                        }
                    }
                    R.id.mine_item_my_moment -> {
                        mMomentService?.goMyMomentList(fragmentActivity)
                    }
                    R.id.mine_item_my_order -> {
                        mMallService?.goMyOrder(fragmentActivity)
                    }
                    R.id.mine_item_my_address_manage -> {
                        mMallService?.goUserAddressManage(fragmentActivity)
                    }
                }
            })
            //图片Item
            register(MineImageItemModel::class.java, MineImageItemViewBinder { _, item ->
                when (item.itemId) {
                    R.id.mine_item_avatar -> {
                        val userId = mLoginService?.getUserId() ?: ""
                        val avatarUrl = mUserInfo?.avatar ?: ""
                        mMineService?.goModifyAvatar(fragmentActivity, userId, avatarUrl)
                    }
                    R.id.mine_item_qrcode -> {
                        MineUIHelper.goMyQrCode(fragmentActivity)
                    }
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //更新头像
        BroadcastRegistry(lifecycleOwner).register(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val newAvatarUrl = intent?.getStringExtra(AppConstant.Key.AVATAR_URL) ?: ""
                Observable.fromIterable(mListItems)
                    .filter {
                        it is MineImageItemModel && it.itemId == R.id.mine_item_avatar
                    }.cast(MineImageItemModel::class.java)
                    .map {
                        mUserInfo?.avatar = newAvatarUrl
                        it.imageUrl = newAvatarUrl
                    }
                    .lifecycle(lifecycleOwner)
                    .subscribe {
                        mListAdapter.notifyDataSetChanged()
                    }
            }
        }, AppConstant.Action.UPDATE_AVATAR)
        //更新昵称
        BroadcastRegistry(lifecycleOwner).register(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val newNickName = intent?.getStringExtra(AppConstant.Key.NICK_NAME) ?: ""
                Observable.fromIterable(mListItems)
                    .filter {
                        it is MineTextItemModel && it.itemId == R.id.mine_item_nickname
                    }.cast(MineTextItemModel::class.java)
                    .map {
                        mUserInfo?.nickname = newNickName
                        it.text = newNickName
                    }
                    .lifecycle(lifecycleOwner)
                    .subscribe {
                        mListAdapter.notifyDataSetChanged()
                    }
            }
        }, AppConstant.Action.UPDATE_NICKNAME)
        //注册位置信息
        mRxLocation.getLocation(fragmentActivity)
            .lifecycle(lifecycleOwner)
            .subscribe({ event ->
                //没有开启位置
                if (event.isNotOpenLocation) {
                    return@subscribe
                }
                val userId = mLoginService?.getUserId()
                if (userId.isNullOrBlank()) {
                    return@subscribe
                }
                mMinePresenter.updateUserPosition(userId, event.longitude, event.latitude)
                    .ioToMain()
                    .lifecycle(lifecycleOwner)
                    .subscribe({
                        if (handlerErrorCode(it)) {
                            LogUtils.d("上传位置信息成功")
                        }
                    }, {
                        it.printStackTrace()
                        LogUtils.d("上传位置信息失败")
                    })
            }, {
                it.printStackTrace()
                LogUtils.d("获取位置信息失败")
            })
    }

    override fun onInflaterViewId(): Int {
        return R.layout.mine_mine_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            setTitle(R.string.mine_mine)
            addRightImageButton(R.drawable.mine_setting, R.id.mine_item_setting)
                .click {
                    mSettingService?.goSetting(fragmentActivity)
                }
            addRightImageButton(R.drawable.mine_notice, R.id.mine_item_notice)
                .click {
                    mNoticeService?.goNotice(fragmentActivity)
                }.longClick {
                    mMineService?.goWebCollectList(fragmentActivity)
                    true
                }
        }
        vRefreshList.apply {
            layoutManager = LinearLayoutManager(fragmentActivity)
            adapter = mListAdapter
        }
    }

    override fun setData() {
        super.setData()
        //先渲染空数据
        render("", "", "")
    }

    override fun onResume() {
        super.onResume()
        //再拉取远程数据
        getUserInfo()
    }

    private fun getUserInfo() {
        mLoginService?.run {
            val userId = getUserId()
            mMinePresenter.getUserInfo(userId)
                .ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({ httpModel ->
                    if (handlerErrorCode(httpModel)) {
                        httpModel.data?.let {
                            //保存用户信息
                            mUserInfo = it
                            render(
                                it.avatar,
                                it.nickname,
                                it.username
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
     */
    private fun render(avatar: String, nickname: String, account: String) {
        mListItems.clear()
        //头像
        mListItems.add(
            MineImageItemModel(
                R.id.mine_item_avatar,
                getString(R.string.mine_avatar),
                avatar,
                R.drawable.base_avatar_round,
                isCanClick = true,
                isCircleImage = true
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
                "",
                R.drawable.mine_qrcode,
                true
            )
        )
        //我的动态
        mListItems.add(
            MineTextItemModel(
                R.id.mine_item_my_moment,
                getString(R.string.mine_my_moment),
                "",
                true
            )
        )
        //我的订单
        mListItems.add(
            MineTextItemModel(
                R.id.mine_item_my_order,
                getString(R.string.mine_my_order),
                "",
                true
            )
        )
        //地址管理
        mListItems.add(
            MineTextItemModel(
                R.id.mine_item_my_address_manage,
                getString(R.string.mine_my_address_manage),
                "",
                true
            )
        )
        mListAdapter.notifyDataSetChanged()
    }
}