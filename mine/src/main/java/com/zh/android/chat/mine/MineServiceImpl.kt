package com.zh.android.chat.mine

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.http.HttpModel
import com.zh.android.chat.mine.http.MinePresenter
import com.zh.android.chat.mine.ui.fragment.MineFragment
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.core.BaseModuleService
import com.zh.android.chat.service.ext.startNavigation
import com.zh.android.chat.service.module.mine.MineService
import com.zh.android.chat.service.module.mine.model.User
import io.reactivex.Observable

/**
 * @author wally
 * @date 2020/08/26
 */
@Route(path = ARouterUrl.MINE_SERVICE, name = "我的模块服务")
class MineServiceImpl : MineService, BaseModuleService() {
    private val mMinePresenter by lazy {
        MinePresenter()
    }

    override fun init(context: Context?) {
    }

    override fun getMineFragment(): String {
        return MineFragment::class.java.name
    }

    override fun getUserInfo(userId: String): Observable<HttpModel<User>> {
        return mMinePresenter.getUserInfo(userId)
    }

    override fun goModifyAvatar(activity: Activity, userId: String, avatarUrl: String) {
        ARouter.getInstance()
            .build(ARouterUrl.MINE_MODIFY_AVATAR)
            .withString(AppConstant.Key.USER_ID, userId)
            .withString(AppConstant.Key.AVATAR_URL, avatarUrl)
            .startNavigation(activity)
    }
}