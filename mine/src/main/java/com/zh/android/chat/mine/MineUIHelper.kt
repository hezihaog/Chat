package com.zh.android.chat.mine

import android.app.Activity
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.startNavigation

/**
 * @author wally
 * @date 2020/08/27
 */
class MineUIHelper private constructor() {
    companion object {
        /**
         * 跳转到修改昵称
         */
        fun goModifyNickname(
            activity: Activity,
            userId: String,
            originNickName: String
        ) {
            ARouter.getInstance()
                .build(ARouterUrl.MINE_MODIFY_NICKNAME)
                .withString(AppConstant.Key.USER_ID, userId)
                .withString(AppConstant.Key.NICK_NAME, originNickName)
                .startNavigation(activity)
        }
    }
}