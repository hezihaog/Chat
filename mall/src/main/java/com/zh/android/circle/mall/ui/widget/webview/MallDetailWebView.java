package com.zh.android.circle.mall.ui.widget.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import androidx.fragment.app.FragmentActivity;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.ycbjie.webviewlib.base.X5WebChromeClient;
import com.ycbjie.webviewlib.base.X5WebViewClient;
import com.ycbjie.webviewlib.client.JsX5WebViewClient;
import com.ycbjie.webviewlib.view.X5WebView;

/**
 * @author wally
 * @date 2020/10/27
 */
public class MallDetailWebView extends X5WebView {
    private static final String URL = "file:///android_asset/viewpoint/mall_goods_detail.html";

    /**
     * 是否初始化了
     */
    private boolean isInit;
    /**
     * 自定义WebViewClient
     */
    private JsX5WebViewClient webViewClient;

    public MallDetailWebView(Context context) {
        super(context);
    }

    public MallDetailWebView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    /**
     * 配置方法
     *
     * @param detailContent 要加载的Web资源内容
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void setup(FragmentActivity activity, String detailContent) {
        if (isInit) {
            return;
        }
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        //注入API对象到Web端的window中
        addJavascriptInterface(new MallWebJsHandler(activity, new MallWebJsHandler.Callback() {
            @Override
            public void injectContent() {
                //被Web端回调时回调
                String format = "javascript:WebFillContent('%s');";
                loadUrl(String.format(format, detailContent));
            }
        }), "WKEventClient");
        isInit = true;
    }

    @Override
    public X5WebViewClient getCustomWebViewClient() {
        if (webViewClient == null) {
            webViewClient = new JsX5WebViewClient(this, getContext()) {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    //页面加载完毕，通知Web端，现在可以开始处理了
                    String format = "javascript:WebReady();";
                    loadUrl(format);
                }
            };
        }
        return webViewClient;
    }

    @Override
    public X5WebChromeClient getCustomWebChromeClient() {
        return super.getCustomWebChromeClient();
    }

    /**
     * 加载详情
     */
    public void loadDetail() {
        loadUrl(URL);
    }
}