/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.zeller.fastlibrary.huangchuang.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lidroid.xutils.http.client.HttpRequest;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.util.StringUtil;

/**
 * 修改密码 已登录状态下
 */
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private ImageView back;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText newPassword2;
    private Button submit;

    // 修改密码接口
    private ChangePasswordApi changePasswordApi;

    private void assignViews() {
        back = (ImageView) findViewById(R.id.back);
        oldPassword = (EditText) findViewById(R.id.oldPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        newPassword2 = (EditText) findViewById(R.id.newPassword2);
        submit = (Button) findViewById(R.id.submit);
    }

    private void initViews() {
        View[] views = {back, submit};
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = App.me().getUser();
        if (user != null) {
            // 初始化修改密码接口
            changePasswordApi = new ChangePasswordApi(this);
            setContentView(R.layout.activity_change_password);
            assignViews();
            initViews();
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.submit:
                submit();
                break;
        }
    }

    private void submit() {
        String oldPasswordText = oldPassword.getText().toString();
        String passwordText = newPassword.getText().toString();
        String password2Text = newPassword2.getText().toString();
        if (oldPasswordText.length() == 0) {
            App.me().toast("请输入原密码");
            oldPassword.requestFocus();
            return;
        }else if (passwordText.length() == 0){
            App.me().toast("请输入新密码");
            newPassword.requestFocus();
            return;
        }else if (!StringUtil.matchesPassword(passwordText)) {
            App.me().toast("密码格式不正确");
            newPassword.requestFocus();
            return;
        }else if (password2Text.length() == 0) {
            App.me().toast("请输入确认密码");
            newPassword2.requestFocus();
            return;
        }else if (!password2Text.equals(passwordText)) {
            App.me().toast("两次输入密码不一致");
            newPassword2.requestFocus();
            return;
        }
        // 调用接口提交
        User user = App.me().getUser();
        if (null!=user){
            changePasswordApi.submit(user.getUuid(),oldPasswordText, passwordText);
        }

    }

    private class ChangePasswordApi extends HttpUtil {


        private ChangePasswordApi(Context context) {
            super(context);

        }

        private void submit(String uuid, String oldPassword, String newPassword) {

            send(
                    HttpRequest.HttpMethod.POST,
                    "hcdj/phoneUserOnlineController.do?updatePassword", // 接口地址
                    "uuid", uuid,
                    "oldpassword", oldPassword, // 旧密码md5
                    "password", newPassword // 新密码md5
            );
        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                finish(); // 修改成功则返回到上一页
            }
            App.me().toast(apiMsg.getMessage());
        }

    }

}
