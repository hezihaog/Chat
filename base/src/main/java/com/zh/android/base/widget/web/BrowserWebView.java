package com.zh.android.base.widget.web;

import android.content.Context;
import android.util.AttributeSet;

import com.tencent.smtt.sdk.WebView;
import com.ycbjie.webviewlib.base.X5WebChromeClient;
import com.ycbjie.webviewlib.base.X5WebViewClient;
import com.ycbjie.webviewlib.client.JsX5WebViewClient;
import com.ycbjie.webviewlib.view.X5WebView;

/**
 * @author wally
 * @date 2021/01/28
 */
public class BrowserWebView extends X5WebView {
    /**
     * 自定义WebViewClient
     */
    private JsX5WebViewClient webViewClient;
    /**
     * 自定义WebChromeClient
     */
    private X5WebChromeClient webChromeClient;
    /**
     * 回调
     */
    private Callback mCallback;

    public BrowserWebView(Context context) {
        super(context);
    }

    public BrowserWebView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    public X5WebViewClient getCustomWebViewClient() {
        if (webViewClient == null) {
            webViewClient = new JsX5WebViewClient(this, getContext()) {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (mCallback != null) {
                        mCallback.onPageFinished(view, url);
                    }
                }

                @Override
                public boolean onCustomShouldOverrideUrlLoading(String url) {
                    //因为简书跳转有问题，将简书的所有跳珠屏蔽
                    if (url.startsWith("jianshu://")) {
                        return true;
                    }
                    return super.onCustomShouldOverrideUrlLoading(url);
                }
            };
        }
        return webViewClient;
    }

    @Override
    public X5WebChromeClient getCustomWebChromeClient() {
        if (webChromeClient == null) {
            webChromeClient = new X5WebChromeClient(this, getContext());
        }
        return webChromeClient;
    }

    public interface Callback {
        void onPageFinished(WebView view, String url);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }
}