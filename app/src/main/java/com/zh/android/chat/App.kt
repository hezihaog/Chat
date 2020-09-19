package com.zh.android.chat

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.alibaba.android.arouter.launcher.ARouter
import com.lzy.ninegrid.NineGridView
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
import com.zh.android.base.ext.loadUrlImage
import com.zh.android.base.util.activity.ActivityProvider
import com.zh.android.base.util.monitor.AppMonitor
import com.zh.android.chat.service.module.base.interceptor.RequestProcessor
import com.zh.android.imageloader.ImageLoader
import com.zh.android.imageloader.strategy.impl.GlideLoader
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
        initImage()
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
            //添加自定义拦截器
            RequestProcessor.getInstance().with(this)
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
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context: Context?, _: RefreshLayout? ->
            MaterialHeader(
                context
            )
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context: Context, _: RefreshLayout? ->
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

    /**
     * 初始化图片加载
     */
    private fun initImage() {
        //图片加载
        ImageLoader.get(this).loader = GlideLoader()
        //九宫格图片控件
        NineGridView.setImageLoader(object : NineGridView.ImageLoader {
            override fun onDisplayImage(context: Context?, imageView: ImageView?, url: String?) {
                imageView?.loadUrlImage(url, R.drawable.ic_default_image)
            }

            override fun getCacheImage(url: String?): Bitmap? {
                return null
            }
        })
    }
}