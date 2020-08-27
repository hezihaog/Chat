package com.zh.android.chat.friend.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.friend.R
import com.zh.android.chat.friend.ui.fragment.UserProfileFragment

/**
 * @author wally
 * @date 2020/08/27
 * 用户资料页面
 */
@Route(path = ARouterUrl.FRIEND_USER_PROFILE)
class UserProfileActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(UserProfileFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, UserProfileFragment.newInstance(
                    Bundle(intent.extras)
                )
            )
        }
    }
}