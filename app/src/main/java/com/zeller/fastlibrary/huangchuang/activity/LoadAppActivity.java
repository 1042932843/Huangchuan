package com.zeller.fastlibrary.huangchuang.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.view.CustomWebView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoadAppActivity extends BaseActivity {

    @Bind(R.id.webView)
    CustomWebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_app);
        ButterKnife.bind(this);
        String url = getIntent().getStringExtra("url");
        mWebview.setWebViewClient(new CustomWebViewClient());
        mWebview.setDownloadListener(new MyWebViewDownLoadListener());
        mWebview.loadUrl(url);
    }

    // 允许点击外链跳转浏览器
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String temp = url.toLowerCase();
            Log.d("AppWebView", "url:" + url);
            if (!temp.startsWith("http://") && !temp.startsWith("https://")) {
                url = "http://" + url;
                view.loadUrl(url);
            } else if (url.indexOf("tel:") != -1) {
                Uri uri = Uri.parse(url);
                Intent it = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(it);
            } else {
                view.loadUrl(url);
            }
            return true;
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }

    @OnClick(R.id.back)
    void onBack(){
        onBackPressed();
    }
}
