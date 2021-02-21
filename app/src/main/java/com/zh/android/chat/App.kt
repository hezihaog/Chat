package com.zh.android.chat

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.apkfuns.logutils.LogUtils
import com.blankj.utilcode.util.Utils
import com.lzy.ninegrid.NineGridView
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheEntity
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.cookie.CookieJarImpl
import com.lzy.okgo.cookie.store.DBCookieStore
import com.lzy.okgo.https.HttpsUtils
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.rousetime.android_startup.AndroidStartup
import com.rousetime.android_startup.StartupListener
import com.rousetime.android_startup.StartupManager
import com.rousetime.android_startup.model.CostTimesModel
import com.rousetime.android_startup.model.LoggerLevel
import com.rousetime.android_startup.model.StartupConfig
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.shuyu.gsyvideoplayer.cache.CacheFactory
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.ycbjie.webviewlib.utils.X5WebUtils
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.getAllChildView
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.base.util.BroadcastRegistry
import com.zh.android.base.util.RecyclerViewScrollHelper
import com.zh.android.base.util.activity.ActivityLifecycleCallbacksAdapter
import com.zh.android.base.util.activity.ActivityProvider
import com.zh.android.base.util.monitor.AppMonitor
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.db.AppDatabase
import com.zh.android.chat.service.db.greendao.core.GreenDaoManager
import com.zh.android.chat.service.ext.getSettingService
import com.zh.android.chat.service.module.base.interceptor.RequestProcessor
import com.zh.android.imageloader.ImageLoader
import com.zh.android.imageloader.strategy.impl.GlideLoader
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.OkHttpClient
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager
import java.util.concurrent.TimeUnit

/**
 * @author wally
 * @date 2020/08/26
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        //初始化管理器
        StartupManager.Builder()
            //配置类
            .setConfig(
                StartupConfig.Builder()
                    .setLoggerLevel(LoggerLevel.DEBUG)
                    .setListener(object : StartupListener {
                        override fun onCompleted(
                            totalMainThreadCostTime: Long,
                            costTimesModels: List<CostTimesModel>
                        ) {
                            LogUtils.d("StartupManager初始化完毕，耗时 => $totalMainThreadCostTime")
                        }
                    }).build()
            )
            .addStartup(ToolBoxStartup())
            .addStartup(SwipeBackStartup())
            .addStartup(RxJavaStartup())
            .addStartup(HttpStartup())
            .addStartup(DatabaseStartup())
            .addStartup(RouterStartup())
            .addStartup(RefreshStartup())
            .addStartup(ImageLoaderStartup())
            .addStartup(WebViewStartup())
            .addStartup(VideoPlayerStartup())
            .build(this)
            .start()
            .await()
    }

    /**
     * 工具初始化
     */
    private class ToolBoxStartup : AndroidStartup<String>() {
        override fun callCreateOnMainThread(): Boolean {
            return true
        }

        override fun waitOnMainThread(): Boolean {
            return true
        }

        override fun create(context: Context): String? {
            //前后台监听
            AppMonitor.get().initialize(context)
            //全局Activity管理
            ActivityProvider.initialize()
            //初始化通用工具类
            Utils.init(context)
            return this.javaClass.simpleName
        }
    }

    /**
     * RxJava初始化
     */
    private class RxJavaStartup : AndroidStartup<String>() {
        override fun callCreateOnMainThread(): Boolean {
            return true
        }

        override fun waitOnMainThread(): Boolean {
            return false
        }

        override fun create(context: Context): String? {
            //设置全局错误处理器
            RxJavaPlugins.setErrorHandler {
                it.printStackTrace()
            }
            return this.javaClass.simpleName
        }
    }

    /**
     * 网络库初始化
     */
    private class HttpStartup : AndroidStartup<String>() {
        override fun callCreateOnMainThread(): Boolean {
            return true
        }

        override fun waitOnMainThread(): Boolean {
            return true
        }

        override fun create(context: Context): String? {
            val loggingInterceptor = HttpLoggingInterceptor("[OkGo][Circle]").apply {
                setPrintLevel(HttpLoggingInterceptor.Level.BODY)
                setColorLevel(java.util.logging.Level.INFO)
            }
            val builder = OkHttpClient.Builder().apply {
                if (BuildConfig.DEBUG_ENABLE) {
                    addInterceptor(loggingInterceptor)
                }
                readTimeout(15000L, TimeUnit.MILLISECONDS)
                writeTimeout(15000L, TimeUnit.MILLISECONDS)
                connectTimeout(15000L, TimeUnit.MILLISECONDS)
                cookieJar(CookieJarImpl(DBCookieStore(context)))
                //信任所有证书,不安全有风险
                val sslParams = HttpsUtils.getSslSocketFactory()
                sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                //添加自定义拦截器
                RequestProcessor.getInstance().with(this)
            }
            //其他统一的配置
            OkGo.getInstance().init(context as Application?)
                .setOkHttpClient(builder.build())
                .setCacheMode(CacheMode.NO_CACHE)
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                .retryCount = 0
            return this.javaClass.simpleName
        }
    }

    /**
     * 数据库初始化
     */
    private class DatabaseStartup : AndroidStartup<String>() {
        override fun callCreateOnMainThread(): Boolean {
            return true
        }

        override fun waitOnMainThread(): Boolean {
            return true
        }

        override fun create(context: Context): String? {
            //初始化Room
            AppDatabase.initialize(context)
            //初始化GreenDao
            GreenDaoManager.init(context)
            return this.javaClass.simpleName
        }
    }

    /**
     * 侧滑关闭
     */
    private class SwipeBackStartup : AndroidStartup<String>() {
        override fun callCreateOnMainThread(): Boolean {
            return true
        }

        override fun waitOnMainThread(): Boolean {
            return true
        }

        override fun create(context: Context): String {
            ActivityProvider.get().registerLifecycleCallback(object : ActivityLifecycleCallbacksAdapter() {
                override fun onActivityCreated(
                    activity: Activity,
                    savedInstanceState: Bundle?
                ) {
                    super.onActivityCreated(activity, savedInstanceState)
                    if (activity is BaseActivity) {
                        //初始化侧滑返回
                        activity.initSwipeBack()
                        //创建页面，先根据配置进行切换
                        getSettingService()?.run {
                            activity.setEnableSwipeBack(isEnableSwipeBack())
                        }
                        //注册，切换侧滑返回的广播，动态切换
                        BroadcastRegistry(activity.lifecycleOwner)
                            .register(object : BroadcastReceiver() {
                                override fun onReceive(context: Context?, intent: Intent?) {
                                    intent?.run {
                                        val isEnable =
                                            getBooleanExtra(
                                                AppConstant.Key.IS_ENABLE_SWIPE_BACK,
                                                true
                                            )
                                        activity.setEnableSwipeBack(isEnable)
                                    }
                                }
                            }, AppConstant.Action.CHANGE_SWIPE_BACK_ENABLE)
                    }
                }
            })
            return this.javaClass.simpleName
        }
    }

    /**
     * 路由初始化
     */
    private class RouterStartup : AndroidStartup<String>() {
        override fun callCreateOnMainThread(): Boolean {
            return true
        }

        override fun waitOnMainThread(): Boolean {
            return true
        }

        override fun create(context: Context): String? {
            if (BuildConfig.DEBUG_ENABLE) {
                ARouter.openLog()
                ARouter.openDebug()
            }
            ARouter.init(context.applicationContext as Application?)
            return this.javaClass.simpleName
        }
    }

    /**
     * 刷新库初始化
     */
    private class RefreshStartup : AndroidStartup<String>() {
        override fun callCreateOnMainThread(): Boolean {
            return true
        }

        override fun waitOnMainThread(): Boolean {
            return true
        }

        override fun create(context: Context): String {
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { _: Context?, _: RefreshLayout? ->
                MaterialHeader(
                    context
                )
            }
            SmartRefreshLayout.setDefaultRefreshFooterCreator { _: Context, _: RefreshLayout? ->
                ClassicsFooter(context).apply {
                    setDrawableSize(20f)
                    setBackgroundColor(ActivityCompat.getColor(context, R.color.base_list_divider))
                }
            }
            return this.javaClass.simpleName
        }
    }

    /**
     * 图片库初始化
     */
    private class ImageLoaderStartup : AndroidStartup<String>() {
        override fun callCreateOnMainThread(): Boolean {
            return true
        }

        override fun waitOnMainThread(): Boolean {
            return true
        }

        override fun create(context: Context): String? {
            //图片加载
            ImageLoader.get(context).loader = GlideLoader()
            //九宫格图片控件
            NineGridView.setImageLoader(object : NineGridView.ImageLoader {
                override fun onDisplayImage(
                    context: Context?,
                    imageView: ImageView?,
                    url: String?
                ) {
                    imageView?.loadUrlImage(url, R.drawable.base_def_img_rect)
                }

                override fun getCacheImage(url: String?): Bitmap? {
                    return null
                }
            })
            //注册页面列表滚动
            ActivityProvider.get()
                .registerLifecycleCallback(object : ActivityLifecycleCallbacksAdapter() {
                    override fun onActivityCreated(
                        activity: Activity,
                        savedInstanceState: Bundle?
                    ) {
                        super.onActivityResumed(activity)
                        val rootView: View = activity.findViewById(android.R.id.content) ?: return
                        if (rootView is ViewGroup) {
                            rootView.getAllChildView().forEach {
                                if (it is RecyclerView) {
                                    RecyclerViewScrollHelper().attachRecyclerView(it, object :
                                        RecyclerViewScrollHelper.SimpleCallback() {
                                        override fun onScrollStateChanged(
                                            recyclerView: RecyclerView,
                                            newState: Int
                                        ) {
                                            super.onScrollStateChanged(recyclerView, newState)
                                            ImageLoader.get(context).loader.run {
                                                //列表滚动时，暂停图片请求
                                                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                                                    pause(context)
                                                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                                    //列表停止时，再恢复图片请求
                                                    resume(context)
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                })
            return this.javaClass.simpleName
        }
    }

    /**
     * WebView初始化
     */
    private class WebViewStartup : AndroidStartup<String>() {
        override fun callCreateOnMainThread(): Boolean {
            return true
        }

        override fun waitOnMainThread(): Boolean {
            return false
        }

        override fun create(context: Context): String? {
            X5WebUtils.init(context)
            return this.javaClass.simpleName
        }
    }

    /**
     * 视频播放器初始化
     */
    private class VideoPlayerStartup : AndroidStartup<String>() {
        override fun callCreateOnMainThread(): Boolean {
            return true
        }

        override fun waitOnMainThread(): Boolean {
            return false
        }

        override fun create(context: Context): String? {
            //切换播放器内核
            PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
            //设置缓存管理器
            CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)
            //GLSurfaceView、支持滤镜
            GSYVideoType.setRenderType(GSYVideoType.GLSURFACE)
            return this.javaClass.simpleName
        }
    }
}