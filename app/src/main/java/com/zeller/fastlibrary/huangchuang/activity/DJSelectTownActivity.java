package com.zeller.fastlibrary.huangchuang.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.util.Constant;
import com.zeller.fastlibrary.huangchuang.view.AppWebView;


public class DJSelectTownActivity extends Activity implements View.OnClickListener {
    public static final String REQUEST_ID = "id";
    private AppWebView webView;
    private Handler handler = new Handler();
//    private PullToRefreshLayout pull;

    private void assignViews() {

        webView = (AppWebView) findViewById(R.id.webView);
//
    }


    private void initViews() {


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
//        pull.post(new Runnable() {
//            @Override
//            public void run() {
//                pull.autoRefresh();
//            }
//        });
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

      webView.loadUrl(Constant.DOMAIN + "/hcdj/app/fp-list/index.html#/");
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
            public void Back() {
                finish();
            }
        }, "ba");

        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void xinwen(String id) {
                if (null!=id){
                    Intent intent = new Intent(DJSelectTownActivity.this, NewsdetailActivity.class);
                    intent.putExtra(NewsdetailActivity.REQUEST_ID, id);
                    startActivity(intent);
                }else {
                    startActivityForResult(new Intent(DJSelectTownActivity.this, LoginActivity.class), LoginActivity.REQUEST_CODE);
                }
            }
        }, "Android");
        // 社情民意
        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void opinio(String id) {
                Intent intent = new Intent(DJSelectTownActivity.this, OpinionActivity.class);
                intent.putExtra(OpinionActivity.REQUEST_NAME, "社情民意");
                intent.putExtra(OpinionActivity.REQUEST_TYPE, "1");
                intent.putExtra(OpinionActivity.REQUEST_ID, id);
                intent.putExtra("apiType", "QZ");
                startActivity(intent);
            }
        }, "op");
        // 约见党代表
        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void part(String id) {
                Intent intent = new Intent(DJSelectTownActivity.this, OpinionActivity.class);
                intent.putExtra(OpinionActivity.REQUEST_NAME, "约见党代表");
                intent.putExtra(OpinionActivity.REQUEST_TYPE, "2");
                intent.putExtra(OpinionActivity.REQUEST_ID, id);
                intent.putExtra("apiType", "QZ");
                startActivity(intent);
            }
        }, "pa");
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



}
