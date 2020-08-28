package com.zh.android.chat.friend.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.friend.R
import com.zh.android.chat.friend.ui.fragment.FriendRequestRecordFragment

/**
 * @author wally
 * @date 2020/08/27
 * 好友请求记录
 */
@Route(path = ARouterUrl.FRIEND_REQUEST_RECORD)
class FriendRequestRecordActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(FriendRequestRecordFragment::class.java) == null) {
            loadRootFragment(R.id.base_container, FriendRequestRecordFragment.newInstance())
        }
    }
}