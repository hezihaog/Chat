package com.zh.android.chat.mine.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.loadMainFragment
import com.zh.android.chat.mine.R
import com.zh.android.chat.mine.ui.fragment.ModifyAvatarFragment

/**
 * @author wally
 * @date 2020/08/31
 * 修改头像
 */
@Route(path = ARouterUrl.MINE_MODIFY_AVATAR)
class ModifyAvatarActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        loadMainFragment(
            R.id.base_container, ModifyAvatarFragment.newInstance(
                Bundle(intent.extras)
            )
        )
    }
}