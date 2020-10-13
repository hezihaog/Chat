package com.zh.android.base.util.web

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.linghit.base.util.argument.bindArgument
import com.ycbjie.webviewlib.inter.InterWebListener
import com.ycbjie.webviewlib.utils.X5WebUtils
import com.ycbjie.webviewlib.view.X5WebView
import com.ycbjie.webviewlib.widget.WebProgress
import com.zh.android.base.R
import com.zh.android.base.core.BaseActivity
import com.zh.android.base.ext.click
import com.zh.android.base.ext.setGone
import com.zh.android.base.ext.toast
import com.zh.android.base.util.ClipboardUtil
import com.zh.android.base.widget.TopBar
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/10/07
 * Web页面
 */
class BrowseActivity : BaseActivity() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vWebView: X5WebView by bindView(R.id.web_view)
    private val vProgress: WebProgress by bindView(R.id.progress)

    /**
     * 要加载的Url
     */
    private val mLoadUrl by bindArgument(ARGS_URL, "")

    companion object {
        private const val ARGS_URL = "args_url"

        /**
         * 跳转
         */
        fun start(activity: Activity, url: String) {
            activity.startActivity(Intent(activity, BrowseActivity::class.java).apply {
                putExtra(ARGS_URL, url)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mLoadUrl.isBlank()) {
            finish()
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
        return R.layout.base_browse_activity
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                finish()
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
                            R.id.open_system_browse -> {
                                openForSystemBrowse(vWebView.url)
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
    }

    /**
     * 系统浏览器打开
     */
    private fun openForSystemBrowse(url: String) {
        val intent = Intent.createChooser(
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            },
            getString(R.string.base_please_choose_browser)
        )
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            toast(R.string.base_your_not_browser)
        }
    }
}