package com.zeller.fastlibrary.huangchuang.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.activity.AccountActivity;
import com.zeller.fastlibrary.huangchuang.activity.BaseActivity;
import com.zeller.fastlibrary.huangchuang.activity.ChoiceVillageActivity;
import com.zeller.fastlibrary.huangchuang.activity.LoadAppActivity;
import com.zeller.fastlibrary.huangchuang.activity.LoginActivity;
import com.zeller.fastlibrary.huangchuang.activity.NewsdetailActivity;
import com.zeller.fastlibrary.huangchuang.activity.SelectTownActivity;
import com.zeller.fastlibrary.huangchuang.util.Constant;
import com.zeller.fastlibrary.huangchuang.view.AppWebView;
import com.zeller.fastlibrary.searchhistory.view.SearchActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class DjCylActivity extends Activity implements View.OnClickListener {
    public static final String REQUEST_ID = "id";
    private AppWebView webView;
    private Handler handler = new Handler();

    private void assignViews() {

        webView = (AppWebView) findViewById(R.id.webView);

    }


    private void initViews() {

//        pull.setPtrHandler(this);
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT < 18) {
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }

//            @Override
//            public void onPageFinished(WebView view, String url) {
//                pull.refreshComplete();
//            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecttown);
        assignViews();
        initViews();
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(Constant.DOMAIN + "/hcdj/app/cyl/index.html#");
        //    webView.loadUrl(Constant.DOMAIN + "/hcdj/app/fpdt/index.html");

        if (webView != null) {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    String id = getIntent().getStringExtra(REQUEST_ID);

                    if (null != id) {
                        webView.loadUrl("javascript:showAndroid('" + id + "')");
                    }
                }
            });
        }

        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void back() {
                finish();
            }
        }, "ba");


        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void xinwen(String id) {
                if (null != id) {
                    Intent intent = new Intent(DjCylActivity.this, NewsdetailActivity.class);
                    intent.putExtra(NewsdetailActivity.REQUEST_ID, id);
                    startActivity(intent);
                } else {
                    startActivityForResult(new Intent(DjCylActivity.this, LoginActivity.class), LoginActivity.REQUEST_CODE);
                }
            }
        }, "Android");

    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyWebView();
    }

    public void destroyWebView() {
        if(webView != null) {
            webView.clearHistory();
            webView.loadUrl("about:blank"); // clearView() should be changed to loadUrl("about:blank"), since clearView() is deprecated now
            webView.freeMemory();
            webView.pauseTimers();
            webView = null; // Note that mWebView.destroy() and mWebView = null do the exact same thing
        }

    }
}