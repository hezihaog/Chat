package com.zh.android.chat.home.ui.activity

import android.Manifest
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.apkfuns.logutils.LogUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import com.youngfeng.snake.annotations.EnableDragToClose
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.lifecycle
import com.zh.android.base.util.DeviceIdUtil
import com.zh.android.base.util.NotificationUtil
import com.zh.android.chat.home.R
import com.zh.android.chat.home.ui.fragment.HomeMainFragment
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.module.conversation.ConversationService
import com.zh.android.chat.service.module.notice.NoticeService

/**
 * @author wally
 * @date 2020/08/26
 */
@EnableDragToClose(value = false)
@Route(path = ARouterUrl.HOME_HOME, extras = AppConstant.Flag.IS_NEED_LOGIN)
class HomeActivity : BaseActivity() {
    @JvmField
    @Autowired(name = ARouterUrl.CONVERSATION_SERVICE)
    var mConversationService: ConversationService? = null
    @JvmField
    @Autowired(name = ARouterUrl.NOTICE_SERVICE)
    var mNoticeService: NoticeService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //检查是否开启通知，没开启马上开启
        if (!NotificationUtil.checkNotificationEnable(this)) {
            NotificationUtil.goNotificationSetting(this)
        }
        //开启会话模块的推送服务
        mConversationService?.startMqttService()
        //开启通知模块的推送服务
        mNoticeService?.startMqttService()
        //获取唯一设备Id
        RxPermissions(this).apply {
            request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
            ).lifecycle(lifecycleOwner)
                .subscribe {
                    val deviceUniqueId =
                        DeviceIdUtil.getDeviceUniqueId(fragmentActivity.application)
                    LogUtils.d("deviceUniqueId：$deviceUniqueId")
                }
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.base_container
    }

    override fun onBindView(view: View?) {
        if (findFragment(HomeMainFragment::class.java) == null) {
            loadRootFragment(R.id.base_container, HomeMainFragment.newInstance())
        }
    }

    override fun onBackPressedSupport() {
        moveTaskToBack(true)
    }
}