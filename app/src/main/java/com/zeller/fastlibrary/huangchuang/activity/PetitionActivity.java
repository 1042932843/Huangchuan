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
 * 组织信访
 */
public class PetitionActivity extends BaseActivity implements View.OnClickListener {
    private Button complain;
    private ImageView back;
    private EditText feedback;
    private EditText name;
    private EditText phone;
    private EditText biaoti;
    private OpinionApi opinionApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petition);
        opinionApi = new OpinionApi(this);
        assignViews();
        initViews();
    }

    private void assignViews() {
        back = (ImageView) findViewById(R.id.back);
        complain = (Button) findViewById(R.id.complain);
        feedback = (EditText) findViewById(R.id.feedback);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        biaoti = (EditText) findViewById(R.id.biaoti);
    }

    private void initViews() {
        View[] views = {complain, back};
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
            case R.id.complain:
                Complain();
                break;

        }
    }

    private void Complain() {
        String feedbackText = feedback.getText().toString();
        String nameText = name.getText().toString();
        String phoneText = phone.getText().toString();
        String biaotiText = biaoti.getText().toString();


        if (biaotiText.length() == 0) {
            App.me().toast("请输入标题");
            biaoti.requestFocus();
            return;
        }


        if (nameText.length() == 0) {
            App.me().toast("请输入信访名字");
            name.requestFocus();
            return;
        }

        if (phoneText.length() == 0) {
            App.me().toast("请输入信访手机号码");
            phone.requestFocus();
            return;
        }

        if (!StringUtil.matchesPhone(phoneText)) {
            App.me().toast("手机号码格式不正确");
            phone.requestFocus();
            return;
        }

        if (feedbackText.length() == 0) {
            App.me().toast("请输入内容");
            feedback.requestFocus();
            return;
        }


        User user = App.me().getUser();
                if (null!=user){
                    if (opinionApi != null) {
                        opinionApi.Opinion(user.getUuid(), nameText, phoneText,biaotiText, feedbackText);
                    }
                }



    }

    private class OpinionApi extends HttpUtil {

        private OpinionApi(Context context) {
            super(context); // 传递上下文, 初始化进度对话框
        }


        public void Opinion(String uuid, String name, String phone, String title, String detail) {

            send(HttpRequest.HttpMethod.POST,
                    "hcdj/phoneZzxfController.do?addZzxf",
                    "uuid", uuid,
                    "applicantName", name,
                    "applicantPhone", phone,
                    "title", title,
                    "detail", detail
            );
        }


        @Override
        public void onSuccess(ApiMsg apiMsg) {
            if (apiMsg.isSuccess()) {
                App.me().toast(apiMsg.getMessage());
                finish();
            } else {
                App.me().toast(apiMsg.getMessage());
            }
        }

    }

}
