package com.zh.android.chat

import android.app.Application
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheEntity
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.cookie.CookieJarImpl
import com.lzy.okgo.cookie.store.DBCookieStore
import com.lzy.okgo.https.HttpsUtils
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.zh.android.base.util.activity.ActivityProvider
import com.zh.android.base.util.monitor.AppMonitor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * @author wally
 * @date 2020/08/26
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initAppMonitor()
        initHttp()
        initRouter()
        initRefresh()
        initActivityProvider()
    }

    /**
     * 初始化前后台监听
     */
    private fun initAppMonitor() {
        AppMonitor.get().initialize(this)
    }

    private fun initHttp() {
        val loggingInterceptor = HttpLoggingInterceptor("[OkGo][Chat]").apply {
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
            cookieJar(CookieJarImpl(DBCookieStore(this@App)))
            //信任所有证书,不安全有风险
            val sslParams = HttpsUtils.getSslSocketFactory()
            sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
        }
        //其他统一的配置
        OkGo.getInstance().init(this)
            .setOkHttpClient(builder.build())
            .setCacheMode(CacheMode.NO_CACHE)
            .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE).retryCount = 0
    }

    /**
     * 初始化路由器
     */
    private fun initRouter() {
        if (BuildConfig.DEBUG_ENABLE) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
    }

    private fun initRefresh() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context: Context?, layout: RefreshLayout? ->
            MaterialHeader(
                context
            )
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context: Context, layout: RefreshLayout? ->
            val footer = ClassicsFooter(context)
            footer.setDrawableSize(20f)
            footer.setBackgroundColor(context.resources.getColor(R.color.base_list_divider))
            footer
        }
    }

    /**
     * 初始化全局Activity管理
     */
    private fun initActivityProvider() {
        ActivityProvider.initialize()
    }
}