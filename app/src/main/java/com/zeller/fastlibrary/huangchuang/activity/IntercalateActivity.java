package com.zeller.fastlibrary.huangchuang.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.http.client.HttpRequest;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.view.DataCleanManager;

import org.simple.eventbus.EventBus;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/6/8 0008.
 */
public class IntercalateActivity extends BaseActivity implements View.OnClickListener {
    public static final int REQUEST_CODE = 'i' + 'n' + 't' + 'e' + 'r';

    private RelativeLayout modifypassword;
    private RelativeLayout Signout;
    private RelativeLayout rela_huancuen;
    private SignoutApi signoutApi;
    private ImageView back;
    private TextView huancun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercalate);
        signoutApi = new SignoutApi(this);
        assignViews();
        initViews();
        try {
            huancun.setText(DataCleanManager.getTotalCacheSize(IntercalateActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void assignViews() {
        modifypassword = (RelativeLayout) findViewById(R.id.modifypassword);
        Signout = (RelativeLayout) findViewById(R.id.Signout);
        rela_huancuen = (RelativeLayout) findViewById(R.id.rela_huancuen);
        back = (ImageView) findViewById(R.id.back);
        huancun = (TextView) findViewById(R.id.huancun);

    }

    private void initViews() {
        View[] views = {modifypassword, Signout, back, rela_huancuen};
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.modifypassword:
                Modifypassword();
                break;
            case R.id.Signout:
                Signout();
                break;
            case R.id.rela_huancuen:
                Huancuen();
                break;
        }
    }

    private void Huancuen() {
        DataCleanManager.clearAllCache(IntercalateActivity.this);
        App.me().toast("清除成功");
        try {
            huancun.setText(DataCleanManager.getTotalCacheSize(IntercalateActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Signout() {
        User user = App.me().getUser();
        if (null != user) {
            signoutApi.Signout(user.getUuid());
        }

    }

    private void Modifypassword() {
        Intent intent = new Intent(IntercalateActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    private class SignoutApi extends HttpUtil {


        private SignoutApi(Context context) {
            super(context);

        }

        private void Signout(String uuid) {

            send(
                    HttpRequest.HttpMethod.POST,
                    "hcdj/phoneUserOnlineController.do?logout", // 接口地址
                    "uuid", uuid
            );
        }

        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                User login = App.me().login();
                App.me().logout();
//                 DataCleanManager.clearAllCache(IntercalateActivity.this);
                EventBus.getDefault().post(login, "UserFragment.onLogoutSuccess");
                App.me().toast("退出成功");
                JPushInterface.setAlias(IntercalateActivity.this,"",null);
                JPushInterface.clearAllNotifications(IntercalateActivity.this);
                setResult(RESULT_OK); // 回调登录成功
                finish();
//               startActivityForResult(new Intent(IntercalateActivity.this, LoginActivity.class), LoginActivity.REQUEST_CODE);
            }else{
                App.me().toast(apiMsg.getMessage());
            }

        }

    }

}
