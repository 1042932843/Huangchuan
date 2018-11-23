package com.zeller.fastlibrary.huangchuang.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.Service.WebSocketService;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.util.StringUtil;

/**
 * Created by Administrator on 2017/6/8 0008.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    public static final int REQUEST_CODE = 'l' + 'o' + 'g' + 'i' + 'n';
    private ImageView back;
//    private PullToRefreshLayout pull;
//    private CustomWebView webView;
//    private AsForjs asForjs;
    private LoginApi loginApi;
    private EditText phone;
    private EditText password;
    private Button login;


    private void assignViews() {
        back = (ImageView) findViewById(R.id.back);
        phone = (EditText) findViewById(R.id.phone);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
//        pull = (PullToRefreshLayout) findViewById(R.id.pull);
//        webView = (CustomWebView) findViewById(R.id.webView);
    }

    private void initViews(String phone) {
        View[] views = {login,back};
        for (View view : views) {
            view.setOnClickListener(this);
        }
        this.phone.setText(phone);
        if (!TextUtils.isEmpty(phone)) {
            password.requestFocus();
        }
//        pull.setPtrHandler(this);
//        WebSettings settings = webView.getSettings();
/*        if (Build.VERSION.SDK_INT < 18) {
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
        });*/
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
        // 读取偏好设置: 已登录过
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", null); // 最后一次登录的手机号码
        loginApi = new LoginApi(this);
        setContentView(R.layout.activity_login);
        assignViews();
        initViews(phone);
//        asForjs = new AsForjs(this);
//        asForjs.setCallBack(this);


//        webView.loadUrl(Constant.DOMAIN + "/hcdj/app/login.html");
//        webView.addJavascriptInterface(asForjs, "Android");
//        webView.addJavascriptInterface(new Object() {
//            @JavascriptInterface
//            public void showname(String name, String pwd) {
////                Toast.makeText(LoginActivity.this, "用户名：" + name + "密码：" + pwd, Toast.LENGTH_SHORT).show();
//
////                if (!StringUtil.matchesIdCard(name)) {
////                    App.me().toast("用户名格式不正确");
////                    return;
////                }
////                if (!StringUtil.matchesPassword(pwd)) {
////                    App.me().toast("密码格式不正确");
////                    return;
////                }
//
//                if (null != loginApi) {
//                    loginApi.login(name, pwd);
//                }
//            }
//        }, "Android");



    }

/*    @Override
    public void finish() {
        if (webView != null) {
            webView.loadUrl("about:blank");
        }
        super.finish();
    }*/

/*    @Override
    public void onBackPressed() {
        if (webView != null && webView.isCustomViewShowing()) {
            webView.hideCustomView();
        } else {
            super.onBackPressed();
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
//                String str ="我来自android";
//                webView.loadUrl("javascript:showAndroid('"+str+"')");
                finish();
                break;
            case R.id.login:
                login(); // 提交登录
                break;
        }
    }
    // 调用登录
    private void login() {
        String phoneText = phone.getText().toString();
        if (phoneText.length() == 0) {
            App.me().toast("请输入用户名");
            phone.requestFocus();
            return;
        }

        String passwordText = password.getText().toString();
        if (passwordText.length() == 0) {
            App.me().toast("请输入密码");
            password.requestFocus();
            return;
        }
//        if (!StringUtil.matchesPassword(passwordText)) {
//            App.me().toast("密码格式不正确");
//            password.requestFocus();
//            return;
//        }
        // 调动登录接口
        loginApi.login(phoneText, passwordText);
    }


    // 登录接口, 通用
    private class LoginApi extends HttpUtil {

        private String Idcard; // 手机号码
        private String pw; // 密码md5

        private LoginApi(Context context) {
            super(context); // 传递上下文, 初始化进度对话框
        }


        public void login(String Idcard, String pw) {
            if (!StringUtil.matchesMd5(pw)) {
                pw = StringUtil.md5(pw); // 密码md5
            }
            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneUserOnlineController.do?login", // 登录接口地址
                    "idcard", this.Idcard = Idcard,
                    "pw", this.pw = pw
            );
        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                // 登录成功, 保存用户登录信息, 持久化
                String resultInfo = apiMsg.getResult();
                User user = JSON.parseObject(resultInfo, User.class);
                // 绑定登录用户
                App.me().login(user);
                // 保存偏好设置
                SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("phone", Idcard);
//                edit.putInt("type", type);
                edit.commit();
                hideDialog(); // 立即隐藏进度对话框
//                EventBus.getDefault().post("1", "LoginActivity.onLoginSuccess");
                stopService(new Intent(LoginActivity.this, WebSocketService.class));
                App.me().toast("登录成功");
                setResult(RESULT_OK); // 回调登录成功
                finish(); // 返回上一页
            } else {
                App.me().toast(apiMsg.getMessage()); // 登录不成功, 根据接口返回提示
            }
        }

    }
}
