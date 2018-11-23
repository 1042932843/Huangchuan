package com.zeller.fastlibrary.huangchuang.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.activity.AccountActivity;
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

public class HomeFragment extends PagerFragment implements PtrHandler {

    @Bind(R.id.web_view)
    AppWebView mWebView;
    //    private PullToRefreshLayout pull;
    private String switchPage;

    //    ProgressDialog dialog = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

//        pull = (PullToRefreshLayout) view.findViewById(R.id.pull);
        return view;
    }

    public static HomeFragment newInstance(String arg1) {
        Log.d("reg", "HomeFragment:" + arg1);
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("arg1", arg1);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    private static final String APP_CACAHE_DIRNAME = "/data/data/com.zeller.fastlibrary/cache/webviewCache";

    private void initWebView() {
        //设置支持js
        mWebView.getSettings().setJavaScriptEnabled(true);
        //设置渲染效果优先级，高
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        //设置缓存模式
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        String cacheDirPath = APP_CACAHE_DIRNAME;
        //设置数据库缓存路径
        mWebView.getSettings().setDatabasePath(cacheDirPath);
        //设置 应用 缓存目录
        mWebView.getSettings().setAppCachePath(cacheDirPath);
        //开启 DOM 存储功能
        mWebView.getSettings().setDomStorageEnabled(true);
        //开启 数据库 存储功能
        mWebView.getSettings().setDatabaseEnabled(true);
        //开启 应用缓存 功能
        mWebView.getSettings().setAppCacheEnabled(true);
    }

    private void initViews() {
//        pull.setPtrHandler(this);
        WebSettings settings = mWebView.getSettings();
        if (android.os.Build.VERSION.SDK_INT < 18) {
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }
//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//                return true;
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
////                dialog.dismiss();
////                pull.refreshComplete();
//                if (switchPage != null) {
//                    Log.d("reg", "switchPage1 != null---->" + switchPage);
////                    mWebView.loadUrl("javascript:"+switchPage);
//                    mWebView.post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            // 注意调用的JS方法名要对应上
//                            // 调用javascript的callJS()方法
//                            mWebView.loadUrl("javascript:showAndroid(1)");
//                        }
//                    });
//                }
//            }
//        });
    }

//    @Subscriber(tag = "refresh")
//    private void Refresh(String sr) {
//        Log.d("reg", "sr:" + sr);
//        if (null != sr && sr.equals("1")) {
//            Start();
//        }
//    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if (null!=switchPage){
//            switchPage = getArguments().getString("arg1");
//        }


        Start();
        initViews();
        initWebView();
        Bundle bundle = getArguments();
        if (bundle != null) {
            switchPage = bundle.getString("mes");
        }


        //新闻详情
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void xinwen(String id) {
                if (null != id) {
                    Intent intent = new Intent(getActivity(), NewsdetailActivity.class);
                    intent.putExtra(NewsdetailActivity.REQUEST_ID, id);
                    startActivity(intent);
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.REQUEST_CODE);
                }
            }
        }, "Android");

        // 扶贫地图点
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void oose(String id) {
                Intent intent = new Intent(getActivity(), ChoiceVillageActivity.class);
                intent.putExtra(ChoiceVillageActivity.REQUEST_ID, id);
                Log.d("reg", "扶贫地图点:id=" + id);
//                intent.putExtra(ChoiceVillageActivity.REQUEST_NAME, name);
                startActivity(intent);
            }
        }, "coo");

        // 扶贫地图
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void areaid(String areaId) {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                intent.putExtra(AccountActivity.REQUEST_ID, areaId);
                startActivity(intent);
            }
        }, "Andr");
        //搜索
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void search() {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        }, "sea");



        //网上E支部地图
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void Coose(String id) {
                Intent intent = new Intent(getActivity(), SelectTownActivity.class);
                intent.putExtra(SelectTownActivity.REQUEST_ID, id);
                Log.d("reg", "网上E支部地图:id=" + id);
                startActivity(intent);
            }
        }, "ch");

        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void url(String url) {
                Log.d("reg", "url:" + url);
                Intent intent = new Intent(getActivity(), LoadAppActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        }, "downloadApp");

    }


    private void Start() {
//        dialog = ProgressDialog.show(getActivity(),null,"页面加载中，请稍后..");
        String url=Constant.DOMAIN + "/hcdj/app/index.html";
        mWebView.loadUrl(url);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);//允许DCOM
    }
//        if (null != switchPage) {
//            mWebView.setWebViewClient(webviewcilnt);
//        }
//
//    }
//
//    WebViewClient webviewcilnt = new WebViewClient() {
//        @Override
//        public void onReceivedError(WebView view, int errorCode,
//                                    String description, String failingUrl) {
//
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//            mWebView.loadUrl("javascript:showAndroid(1)");
//        }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            super.onPageStarted(view, url, favicon);
//        }
//    };

    public void Wbe(String switchPage) {
        // 必须另开线程进行JS方法调用(否则无法调用)
        mWebView.post(new Runnable() {
            @Override
            public void run() {

                // 注意调用的JS方法名要对应上
                // 调用javascript的callJS()方法
//                mWebView.loadUrl("javascript:showAndroid(1)");
                mWebView.loadUrl("javascript:showAndroid('" + switchPage + "')");

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View content, View header) {
        return mWebView.getScrollY() == 0 && PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
//        mWebView.loadUrl(Constant.DOMAIN + "/hcdj/app/index.html");

//        //结束后调用
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                pull.refreshComplete();
//            }
//        }, 1000);
    }
}
