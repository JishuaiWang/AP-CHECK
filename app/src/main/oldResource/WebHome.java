package com.example.kunrui.apcheck;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebHome extends AppCompatActivity {
    private MemoryOpreate memoryOpreate;
    private WebView mWebView;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_home);

//        memoryOpreate = new MemoryOpreate();
//
//        //获取webview对象，并设置好允许使用js
//        mWebView= findViewById(R.id.web_home);
//        WebSettings webSetting = mWebView.getSettings();
//        webSetting.setJavaScriptEnabled(true);
//        webSetting.setAppCacheEnabled(true);   //启用appCache
//        webSetting.setDatabaseEnabled(true);
//        webSetting.setDomStorageEnabled(true);
//        webSetting.setBlockNetworkImage(true);
//        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        //设置可自由缩放网页、JS生效
//        webSetting.setSupportZoom(true);
//        webSetting.setBuiltInZoomControls(true);
//
//        // 如果页面中链接，如果希望点击链接继续在当前browser中响应，
//        // 而不是新开Android的系统browser中响应该链接，必须覆盖webview的WebViewClient对象
//        mWebView.setWebViewClient(new WebViewClient() {
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
//                view.loadUrl(url);
//                return true;
//            }
//        });
//
//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                mWebView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //获取上边assets文件夹里的jquery文件夹的字节流，并转为String形式
//                        String jsStr = memoryOpreate.readStrAssets(WebHome.this, "js/jquery-2.1.1.min.js");
//                        String tt = memoryOpreate.readStrAssets(WebHome.this, "js/stopExecutionOnTimeout.js");
//
//                        //将上边获取到的js代码串加入到webView中
//                        mWebView.loadUrl("javascript:" + jsStr);
//                        mWebView.loadUrl("javascript:" + tt);
//                        System.out.println("加载完成");
//                    }
//                }, 100);
//            }
//        });
//        mWebView.setWebChromeClient(new WebChromeClient());
//        mWebView.loadUrl("file:///android_asset/index.html");
    }
}