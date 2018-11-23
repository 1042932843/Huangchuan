package com.zeller.fastlibrary.huangchuang.fragments;

import android.annotation.SuppressLint;
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
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.activity.LoginActivity;
import com.zeller.fastlibrary.huangchuang.activity.MaillistActivity;
import com.zeller.fastlibrary.huangchuang.activity.NewsdetailActivity;
import com.zeller.fastlibrary.huangchuang.activity.RedFlagActivity;
import com.zeller.fastlibrary.huangchuang.activity.StudioActivity;
import com.zeller.fastlibrary.huangchuang.activity.VolunteerActivity;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.util.Constant;
import com.zeller.fastlibrary.huangchuang.view.AppWebView;
import com.zeller.fastlibrary.searchhistory.view.SearchActivity;

import org.simple.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class PartyFragment extends PagerFragment  implements PtrHandler {

    @Bind(R.id.web_view)
    AppWebView mWebView;
//    private PullToRefreshLayout pull;
    private String switchPage;
//    ProgressDialog dialog = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_party, container, false);
        ButterKnife.bind(this, view);
//        pull = (PullToRefreshLayout) view.findViewById(R.id.pull);
        initWebView();
        return view;
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
        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//                return true;
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//
//                if (switchPage != null){
//                    Log.d("reg", "switchPage1 != null---->"+switchPage);
//                    mWebView.loadUrl("javascript:"+switchPage);
//                }
//
//                User user = App.me().login();
//                if (user != null){
//                    Log.d("reg", "user.getUuid()="+user.getUuid());
//                    mWebView.loadUrl("javascript:setUuid('"+user.getUuid()+"')");
//                }
////                dialog.dismiss();
//            }
        });



    }
    public  void  Wbe( String switchPage ){
        // 必须另开线程进行JS方法调用(否则无法调用)
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                    Log.d("reg","switchPage:"+switchPage);
                // 注意调用的JS方法名要对应上
                // 调用javascript的callJS()方法
//                mWebView.loadUrl("javascript:showAndroid(1)");
                mWebView.loadUrl("javascript:showAndroid('" + switchPage + "')");

            }
        });
    }


    @SuppressLint("JavascriptInterface")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        mWebView.loadUrl("http://192.168.1.78/hcdj/app/dyzj/index.html");
//        dialog = ProgressDialog.show(getActivity(),null,"页面加载中，请稍后..");
//        if (null!=switchPage){
//            switchPage = getArguments().getString("arg2");
//        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            switchPage = bundle.getString("mes");
            Log.d("reg", "HomeFragmentswitchPage:" + switchPage);
        }
        String url=Constant.DOMAIN+"/hcdj/app/djzj/index.html";
        mWebView.loadUrl(url);
//        mWebView.loadUrl("file:///android_asset/index.html");
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setWebViewClient(new WebViewClient());
        initViews();
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // 新闻详情
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void xinwen(String id) {
                User user = App.me().getUser();
                if (null != user) {
                    Intent intent = new Intent(getActivity(), NewsdetailActivity.class);
                    intent.putExtra(NewsdetailActivity.REQUEST_ID, id);
                    startActivity(intent);
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.REQUEST_CODE);
                }


            }
        }, "Android");

        //用户联系人列表
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void maillist() {
                User user = App.me().getUser();
                if (null != user) {
                    Intent intent = new Intent(getActivity(), MaillistActivity.class);
                    intent.putExtra(MaillistActivity.REQUEST_UUID, user.getUuid());
                    startActivity(intent);
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.REQUEST_CODE);
                }
            }
        }, "And");
        // 流动红旗
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void Red() {
                User user = App.me().getUser();
                if (null != user) {
                    Intent intent = new Intent(getActivity(), RedFlagActivity.class);
//                    intent.putExtra(MaillistActivity.REQUEST_UUID, user.getUuid());
                    startActivity(intent);
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.REQUEST_CODE);
                }
            }
        }, "eedflag");

        // 党代表工作室
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void Repres() {
                User user = App.me().getUser();
                if (null != user) {
                    Intent intent = new Intent(getActivity(), StudioActivity.class);
                    startActivity(intent);
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.REQUEST_CODE);
                }
            }
        }, "Rep");

        // 志愿者活动
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void Volun() {
//                User user = App.me().getUser();
//                if (null != user) {
                    Intent intent = new Intent(getActivity(), VolunteerActivity.class);
                    startActivity(intent);
//                } else {
//                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.REQUEST_CODE);
//                }
            }
        }, "volu");
        //搜索
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void search() {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        }, "sea");


    }

    public static PartyFragment newInstance(String arg1) {
        Log.d("reg","PartyFragment:"+arg1);
        PartyFragment partyFragment = new PartyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("arg2", arg1);
        partyFragment.setArguments(bundle);
        return partyFragment;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginActivity.REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            Log.d("reg","11111:");
            EventBus.getDefault().post("1", "Login");
        }

    }
    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View content, View header) {
        return mWebView.getScrollY() == 0 && PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, content, header);
    }
    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        mWebView.loadUrl(Constant.DOMAIN+"/hcdj/app/djzj/index.html");
//        //结束后调用
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                pull.refreshComplete();
//            }
//        }, 1000);
    }
}
