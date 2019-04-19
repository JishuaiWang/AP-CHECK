package com.example.kunrui.apcheck;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.example.kunrui.apcheck.MethodsClass.FileOpr;

import java.net.URL;

public class FragmentWeb extends Fragment {
    private String url;
    private WebView openWrt;
    private FileOpr fileOpr = new FileOpr();

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.web_view, container, false);
        assert getArguments() != null;
        url = getArguments().getString("Address");
        System.out.println("url = " + url);//获取activity传参值
        return view;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("FragmentWeb 创建");
        openWrt = getActivity().findViewById(R.id.openWrt_web);
        setWebMsg(openWrt);
        openWrt.loadUrl(url);

        TextView MAC = getActivity().findViewById(R.id.MAC);
        TextView rssi = getActivity().findViewById(R.id.RSSI);
        Button forword = getActivity().findViewById(R.id.forward);
        Button back = getActivity().findViewById(R.id.back);
        forword.setOnClickListener(new myListen());
        back.setOnClickListener(new myListen());
        MAC.setText(fileOpr.read_status("BSSID"));
        rssi.setText(fileOpr.read_status("RSSI"));
    }

    public class myListen implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.forward:
                    openWrt.goForward();
                    break;
                case R.id.back:
                    openWrt.goBack();
                    break;
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setWebMsg(WebView openWrt) {
        WebSettings webSettings = openWrt.getSettings();
        webSettings.setJavaScriptEnabled(true); //启用javascript
        webSettings.setAppCacheEnabled(false);   //启用appCache
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//缓存

        //设置可自由缩放网页、JS生效
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        // 如果页面中链接，如果希望点击链接继续在当前browser中响应，
        // 而不是新开Android的系统browser中响应该链接，必须覆盖webview的WebViewClient对象
        openWrt.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });
    }

    //https://blog.csdn.net/raotenghong2611/article/details/83003828 参考
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            url = getArguments().getString("Address");
            Log.e("url:", url);
            openWrt.loadUrl(url);
        }
    }
}