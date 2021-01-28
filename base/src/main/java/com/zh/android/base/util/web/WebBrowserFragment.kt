package com.zh.android.base.util.web

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import androidx.appcompat.widget.PopupMenu
import com.linghit.base.util.argument.bindArgument
import com.tencent.smtt.sdk.CookieSyncManager
import com.ycbjie.webviewlib.inter.InterWebListener
import com.ycbjie.webviewlib.utils.X5WebUtils
import com.ycbjie.webviewlib.widget.WebProgress
import com.zh.android.base.R
import com.zh.android.base.constant.BaseConstant
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.ClipboardUtil
import com.zh.android.base.widget.TopBar
import com.zh.android.base.widget.web.BrowserWebView
import com.zh.android.base.widget.web.WebNavigationBottomBar
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/10/16
 * 内置Web浏览器
 */
class WebBrowserFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vWebView: BrowserWebView by bindView(R.id.web_view)
    private val vProgress: WebProgress by bindView(R.id.progress)
    private val vNavigationBottomBar: WebNavigationBottomBar by bindView(R.id.navigation_bottom_bar)

    /**
     * 是否显示顶部栏
     */
    private val isShowTopBar by bindArgument(BaseConstant.IS_SHOW_TOP_BAR, true)

    /**
     * 要加载的Url
     */
    private val mLoadUrl by bindArgument(BaseConstant.ARGS_URL, "")

    companion object {
        fun newInstance(args: Bundle? = Bundle()): WebBrowserFragment {
            val fragment = WebBrowserFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mLoadUrl.isBlank()) {
            fragmentActivity.finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onResume() {
        super.onResume()
        vWebView.settings.javaScriptEnabled = true
    }

    override fun onStop() {
        super.onStop()
        vWebView.settings.javaScriptEnabled = false
    }

    override fun onDestroy() {
        vWebView.apply {
            stopLoading()
            destroy()
        }
        super.onDestroy()
    }

    override fun onInflaterViewId(): Int {
        return R.layout.base_browse_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            if (isShowTopBar) {
                setVisible()
            } else {
                setGone()
            }
            addLeftBackImageButton().click {
                fragmentActivity.onBackPressed()
            }
            addLeftImageButton(R.drawable.base_close, R.id.topbar_item_close).click {
                fragmentActivity.finish()
            }
            addRightImageButton(R.drawable.base_more, R.id.topbar_item_more).click {
                //更多
                PopupMenu(fragmentActivity, it).apply {
                    menuInflater.inflate(R.menu.base_browse_menu, menu)
                    setOnMenuItemClickListener { menu ->
                        when (menu.itemId) {
                            R.id.base_refresh -> {
                                vWebView.reLoadView()
                                true
                            }
                            R.id.copy_web_path -> {
                                ClipboardUtil.copyToClipboard(fragmentActivity, vWebView.url)
                                toast(R.string.base_copy_success)
                                true
                            }
                            R.id.open_system_browser -> {
                                openForSystemBrowser(vWebView.url)
                                true
                            }
                            R.id.clear_browser_cache -> {
                                //清除浏览器缓存
                                clearBrowserCache()
                                true
                            }
                            R.id.hide_top_bar -> {
                                //隐藏顶部栏
                                vTopBar.setGone()
                                true
                            }
                            else -> {
                                false
                            }
                        }
                    }
                    show()
                }
            }
        }
        vProgress.apply {
            setColor(Color.BLUE)
            show()
        }
        vWebView.apply {
            //取消硬件加速
            setOpenLayerType(false)
            //当页面刷新时，刷新底部导航栏的状态
            setCallback { _, _ ->
                refreshNavigationBottomBar()
            }
            x5WebChromeClient.setWebListener(object : InterWebListener {
                override fun hindProgressBar() {
                    vProgress.setGone()
                }

                override fun showErrorView(type: Int) {
                    when (type) {
                        X5WebUtils.ErrorMode.NO_NET -> {
                            //没有网络
                        }
                        X5WebUtils.ErrorMode.STATE_404 -> {
                            //404，网页无法打开
                        }
                        X5WebUtils.ErrorMode.RECEIVED_ERROR -> {
                            //onReceivedError，请求网络出现error
                        }
                        X5WebUtils.ErrorMode.SSL_ERROR -> {
                            //在加载资源时通知主机应用程序发生SSL错误
                        }
                        else -> {
                        }
                    }
                }

                override fun startProgress(newProgress: Int) {
                    vProgress.setWebProgress(newProgress)
                }

                override fun showTitle(title: String?) {
                    vTopBar.setTitle(title)
                }
            })
            loadUrl(mLoadUrl)
        }
        vNavigationBottomBar.apply {
            setCallBack(object : WebNavigationBottomBar.CallBack {
                override fun onGoBack() {
                    if (vWebView.canGoBack()) {
                        vWebView.goBack()
                    }
                }

                override fun onForward() {
                    if (vWebView.canGoForward()) {
                        vWebView.goForward()
                    }
                }

                override fun onRefresh() {
                    vWebView.reLoadView()
                }

                override fun onCollect(isCollect: Boolean) {
                    //切换收藏
                    setCollect(!isCollect)
                }
            })
        }
    }

    /**
     * 刷新底部导航栏
     */
    private fun refreshNavigationBottomBar() {
        vNavigationBottomBar.setCanGoBack(vWebView.canGoBack())
        vNavigationBottomBar.setCanForward(vWebView.canGoForward())
    }

    override fun onBackPressedSupport(): Boolean {
        //如果顶部栏隐藏，则先显示顶部栏
        if (vTopBar.isHide()) {
            vTopBar.setVisible()
            return true
        }
        //如果WebView可以返回上一页，则返回上一页
        if (vWebView.canGoBack()) {
            vWebView.goBack()
            return true
        }
        return super.onBackPressedSupport()
    }

    /**
     * 系统浏览器打开
     */
    private fun openForSystemBrowser(url: String) {
        val intent = Intent.createChooser(
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            },
            getString(R.string.base_please_choose_browser)
        )
        if (intent.resolveActivity(fragmentActivity.packageManager) != null) {
            startActivity(intent)
        } else {
            toast(R.string.base_your_not_browser)
        }
    }

    /**
     * 清除浏览器缓存
     */
    private fun clearBrowserCache() {
        CookieSyncManager.createInstance(fragmentActivity.applicationContext)
        val cookieManager = CookieManager.getInstance()
        cookieManager.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                removeSessionCookies(null)
                removeAllCookie()
                flush()
            } else {
                removeSessionCookies(null)
                removeAllCookie()
                CookieSyncManager.getInstance().sync()
            }
        }
        toast(R.string.base_operation_success)
    }
}