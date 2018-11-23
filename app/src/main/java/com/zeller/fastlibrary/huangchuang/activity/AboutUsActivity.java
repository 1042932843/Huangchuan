package com.zeller.fastlibrary.huangchuang.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.DebugUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.http.client.HttpRequest;
import com.zeller.fastlibrary.BuildConfig;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.view.AppWebView;
import com.zeller.fastlibrary.huangchuang.view.PullToRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Administrator on 2017/6/21 0021.
 */
public class AboutUsActivity extends BaseActivity implements View.OnClickListener, PtrHandler {
    private AboutUsApi aboutUsApi;
    private ImageView back;
    private PullToRefreshLayout pull;

    private String newsDetail;
    private TextView mVersion,web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sboutus);
        aboutUsApi = new AboutUsApi(this);
        assignViews();
        initViews();
        aboutUsApi.aboutus();
    }

    private void assignViews() {
        back = (ImageView) findViewById(R.id.back);
        pull = (PullToRefreshLayout) findViewById(R.id.pull);
        web = (TextView) findViewById(R.id.web);
        mVersion = (TextView) findViewById(R.id.version);
    }

    private void initViews() {
        mVersion.setText("当前版本号：v"+BuildConfig.VERSION_NAME);
        back.setOnClickListener(this);
        pull.setPtrHandler(this);



    }

    @Override
    public void finish() {

        super.finish();
    }

    //    @Override
    public void onBackPressed() {
//        if (webView != null && webView.isCustomViewShowing()) {
//            webView.hideCustomView();
//        } else {
            super.onBackPressed();
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View content, View header) {
        return web.getScrollY() == 0 && PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        CharSequence charSequence;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            charSequence = Html.fromHtml(newsDetail,Html.FROM_HTML_MODE_LEGACY);
        } else {
            charSequence = Html.fromHtml(newsDetail);
        }
        web.setText(charSequence);
        pull.refreshComplete();
    }

    private class AboutUsApi extends HttpUtil {

        public AboutUsApi(Context context) {
            super(context);
        }

        private void aboutus() {

            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?aboutUs"

            );
        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            super.onSuccess(apiMsg);
            String result = apiMsg.getResult();
            if (apiMsg.isSuccess()) {
                try {
                    JSONObject jsonObect = new JSONObject(result);

                    if (jsonObect.getString("detail") != null) {
                        newsDetail = jsonObect.getString("detail");
                    }
                    pull.post(new Runnable() {
                        @Override
                        public void run() {
                            pull.autoRefresh();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                App.me().toast(apiMsg.getMessage());
            }
        }
    }
}
