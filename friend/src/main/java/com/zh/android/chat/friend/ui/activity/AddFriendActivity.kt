package com.zh.android.chat.friend.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.friend.R
import com.zh.android.chat.friend.ui.fragment.AddFriendFragment

/**
 * @author wally
 * @date 2020/08/27
 * 添加好友页面
 */
@Route(path = ARouterUrl.FRIEND_ADD_FRIEND)
class AddFriendActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(AddFriendFragment::class.java) == null) {
            loadRootFragment(R.id.base_container, AddFriendFragment.newInstance())
        }
    }
}