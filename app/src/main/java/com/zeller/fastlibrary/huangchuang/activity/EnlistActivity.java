package com.zeller.fastlibrary.huangchuang.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.http.client.HttpRequest;
import com.zeller.fastlibrary.R;
import com.zeller.fastlibrary.huangchuang.App;
import com.zeller.fastlibrary.huangchuang.model.ApiMsg;
import com.zeller.fastlibrary.huangchuang.model.User;
import com.zeller.fastlibrary.huangchuang.util.HttpUtil;
import com.zeller.fastlibrary.huangchuang.util.StringUtil;

/**
 * Created by Administrator on 2017/6/16 0016.
 */
public class EnlistActivity extends BaseActivity implements View.OnClickListener {
    public static final String REQUEST_ID = "id";
    public static final int REQUEST_CODE = 'e'+ 'n' + 'l' + 'i' + 's' + 't';
    private EditText name;
    private EditText phone;
    private ImageView back;
    private Button submit;
    private AlertDialog dialog;
    private EnlistApi enlistApi;
    private String id;
    private String phoneText;
    private String nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlist);
        enlistApi = new EnlistApi(this);
        assignViews();
        initViews();
        id = getIntent().getStringExtra(REQUEST_ID);
    }

    private void assignViews() {
        back = (ImageView) findViewById(R.id.back);
        submit = (Button) findViewById(R.id.submit);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
    }

    private void initViews() {
        View[] views = {back, submit};
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
            case R.id.submit:
                Submit();
                break;
        }
    }

    private void Submit() {
        nameText = name.getText().toString();
        if (nameText.length() == 0) {
            App.me().toast("请输入姓名");
            name.requestFocus();
            return;
        }

        phoneText = phone.getText().toString();
        if (phoneText.length() == 0) {
            App.me().toast("请输入手机号码");
            name.requestFocus();
            return;
        }

        if (!StringUtil.matchesPhone(phoneText)) {
            App.me().toast("手机号码格式不正确");
            phone.requestFocus();
            return;
        }

        showAlertDialog("请确认您是否自愿报名该活动");
    }

    public void showAlertDialog(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EnlistActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_normal_layou, null);
        TextView message = (TextView) view.findViewById(R.id.message);
        message.setText(s);
        TextView positiveButton = (TextView) view.findViewById(R.id.positiveButton);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView negativeButton = (TextView) view.findViewById(R.id.negativeButton);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = App.me().getUser();
                if (null != user) {
                    if (null != id) {
                        enlistApi.Enlist(user.getUuid(), id, phoneText, nameText);
                    }
                }else {
                    startActivityForResult(new Intent(EnlistActivity.this, LoginActivity.class), LoginActivity.REQUEST_CODE);
                }
            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

    }

    private class EnlistApi extends HttpUtil {
        private EnlistApi(Context context) {
            super(context); // 传递上下文, 初始化进度对话框
        }


        public void Enlist(String uuid, String id, String phone, String name) {
            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneNewsController.do?volunteerEnroll",
                    "uuid", uuid,
                    "id", id,
                    "phone", phone,
                    "name", name
            );
        }


        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                App.me().toast(apiMsg.getMessage());
                setResult(RESULT_OK); // 回调登录成功
                hideDialog(); // 立即隐藏进度对话框
                finish(); // 返回上一页
            } else {
                App.me().toast(apiMsg.getMessage());
            }
        }

    }
}
