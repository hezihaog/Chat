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
            };
        }
        return webViewClient;
    }

    @Override
    public X5WebChromeClient getCustomWebChromeClient() {
        return new X5WebChromeClient(this, getContext());
    }

    public interface Callback {
        void onPageFinished(WebView view, String url);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }
}