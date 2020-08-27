package com.zh.android.chat.mine.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.chat.mine.R
import com.zh.android.chat.mine.ui.fragment.ModifyNickNameFragment

/**
 * @author wally
 * @date 2020/08/27
 * 修改昵称页面
 */
@Route(path = ARouterUrl.MINE_MODIFY_NICKNAME)
class ModifyNickNameActivity : BaseActivity() {
    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(ModifyNickNameFragment::class.java) == null) {
            loadRootFragment(
                R.id.base_container, ModifyNickNameFragment.newInstance(
                    Bundle(intent.extras)
                )
            )
        }
    }
}